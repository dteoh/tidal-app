/*
 * Tidal, a communications aggregation and notification tool. 
 * Copyright (C) 2010 Douglas Teoh 
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more 
 * details. You should have received a copy of the GNU General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.dteoh.tidal.sources.email.impl;

import static com.dteoh.tidal.util.EDTUtils.outsideEDT;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.ContentType;
import javax.mail.internet.ParseException;
import javax.mail.search.FlagTerm;

import org.jdesktop.application.ResourceMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dteoh.tidal.exceptions.DisconnectedException;
import com.dteoh.tidal.exceptions.DropletCreationException;
import com.dteoh.tidal.exceptions.DropletInitException;
import com.dteoh.tidal.id.ID;
import com.dteoh.tidal.id.IDGenerator;
import com.dteoh.tidal.sources.email.AbstractEmailDroplet;
import com.dteoh.tidal.sources.email.models.EmailSettings;
import com.dteoh.tidal.sources.email.models.Protocol;
import com.dteoh.tidal.util.EDTUtils;
import com.dteoh.tidal.util.HTMLUtils;
import com.dteoh.tidal.util.NetworkUtils;
import com.dteoh.tidal.views.models.DropletModel;
import com.dteoh.tidal.views.models.RippleModel;
import com.dteoh.treasuremap.ResourceMaps;
import com.google.common.collect.Lists;

/**
 * This Droplet is used to handle imap/imaps email services.
 * 
 * @author Douglas Teoh
 */
public final class ImapDroplet extends AbstractEmailDroplet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImapDroplet.class);

    private static final ResourceMap BUNDLE = new ResourceMaps(ImapDroplet.class).build();

    private Store store = null;
    private Folder inbox = null;

    public static ImapDroplet create(final EmailSettings settings) throws DropletCreationException {
        outsideEDT();
        return new ImapDroplet(IDGenerator.generateID(), settings);
    }

    public static ImapDroplet create(final String host, final Protocol protocol, final String username,
            final String password) throws DropletCreationException {
        outsideEDT();

        return new ImapDroplet(IDGenerator.generateID(), host, protocol, username, password);
    }

    private ImapDroplet(final ID identifier, final EmailSettings settings) {
        super(identifier, settings);
    }

    /**
     * @param host
     * @param protocol
     *            must be either imap or imaps
     * @param username
     * @param password
     */
    private ImapDroplet(final ID identifier, final String host, final Protocol protocol, final String username,
            final String password) {
        super(identifier, host, protocol, username, password);
    }

    @Override
    public void destroy() {
        outsideEDT();
        cleanup();
        super.destroy();
    }

    @Override
    public void init() throws DropletInitException, DisconnectedException {
        outsideEDT();

        if (!NetworkUtils.isGoogleReachable()) {
            throw new DisconnectedException();
        }

        // Don't overwrite system properties.
        final Properties props = new Properties(System.getProperties());
        if (settings.getProtocol() == Protocol.imaps) {
            LOGGER.debug("Setting properties");
            props.setProperty("mail.imaps.starttls.enable", "true");
            props.setProperty("mail.imaps.host", settings.getHost());
            props.setProperty("mail.imaps.port", "993");
            props.setProperty("mail.imaps.user", settings.getUsername());
        }

        final Session session = Session.getInstance(props, null);
        LOGGER.debug("Got session");

        // Set up the mailbox to read from
        try {
            LOGGER.debug("Starting MBOX setup");
            store = session.getStore(settings.getProtocol().toString());
            LOGGER.debug("Got MBOX store");
            store.connect(settings.getHost(), settings.getUsername(), settings.getPassword());
            LOGGER.debug("Connected to MBOX store");
            inbox = store.getFolder("INBOX");
            LOGGER.debug("Got inbox");
            inbox.open(Folder.READ_ONLY);
            LOGGER.debug("Inbox opened");
        } catch (final NoSuchProviderException e) {
            LOGGER.error("Init exception", e);
            throw new DropletInitException(e);
        } catch (final MessagingException e) {
            LOGGER.error("Init exception", e);
            throw new DropletInitException(e);
        }

        // All is well, update UI.
        updateUI(EMPTY);
    }

    @Override
    public void update() {
        outsideEDT();

        LOGGER.info("Starting update");

        view.dropletUpdating(true);

        LOGGER.info("Set icon");

        try {
            LOGGER.info("Restarting");

            restart();

            LOGGER.info("Restarted");
        } catch (DropletInitException e) {
            LOGGER.error("Failed to restart IMAP droplet", e);
            // The network connection might be down.
            view.dropletUpdating(false);
            return;
        } catch (DisconnectedException e) {
            LOGGER.error("Network down", e);
            view.dropletUpdating(false);
            return;
        }

        LOGGER.info("Get ripples");
        Iterable<RippleModel> rms = getRipples();
        LOGGER.info("Get ripples done");
        updateUI(rms);
    }

    @Override
    public Iterable<RippleModel> getRipples() {
        outsideEDT();

        if (inbox == null) {
            return EMPTY;
        }

        try {
            // This search option will only download unread messages.
            final FlagTerm searchOption = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            final Message[] messages = inbox.search(searchOption);

            // Make ripple models
            final List<RippleModel> unreadRipples = Lists.newLinkedList();
            for (int i = 0; i < messages.length; i++) {
                Address[] senderAddresses = messages[i].getFrom();
                String subject = messages[i].getSubject();
                Date sent = messages[i].getSentDate();
                String origin = senderAddresses.length > 0 ? senderAddresses[0].toString() : "Unknown";

                ContentType ct = new ContentType(messages[i].getContentType());
                String ctBaseType = ct.getBaseType();
                String content;

                if ("text/plain".equalsIgnoreCase(ctBaseType)) {
                    content = (String) messages[i].getContent();
                } else if ("text/html".equalsIgnoreCase(ctBaseType)) {
                    content = (String) messages[i].getContent();
                    content = HTMLUtils.html2text(content);
                } else if ("multipart".equalsIgnoreCase(ct.getPrimaryType())) {
                    content = extractMultipartContent((Multipart) messages[i].getContent());
                } else {
                    content = "Unhandled content type: " + ctBaseType;
                    LOGGER.info(content);
                }

                RippleModel rm = new RippleModel.Builder(messages[i].getMessageNumber()).origin(origin)
                        .content(content).subject(subject).received(sent.getTime()).build();

                unreadRipples.add(rm);
            }

            return unreadRipples;
        } catch (final MessagingException e) {
            LOGGER.error("Could not download messages", e);
        } catch (final IOException e) {
            LOGGER.error("Could not download message content", e);
        }

        return EMPTY;
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    protected void restart() throws DropletInitException, DisconnectedException {
        cleanup();
        init();
    }

    /**
     * Extract plain text content from the given multipart message.
     * 
     * @param message
     *            The message to extract from.
     * @return The extracted plain text content.
     * @throws ParseException
     *             When the content type fails to parse correctly.
     * @throws IOException
     *             When there is an error retrieving the multipart body content.
     */
    private String extractMultipartContent(final Multipart message) throws ParseException, IOException {
        ContentType ct = new ContentType(message.getContentType());
        String subType = ct.getSubType();

        String multipartContent = "";

        try {
            for (int part = 0; part < message.getCount(); part++) {
                BodyPart bodyPart = message.getBodyPart(part);
                ContentType bodyCT = new ContentType(bodyPart.getContentType());
                LOGGER.debug("Body CT is {}", bodyCT.toString());

                // Supposed to check if disposition is inline or not, but for
                // some reason the messages are being parsed as being inside
                // the attachment part.

                if ("alternative".equalsIgnoreCase(subType)) {
                    // Multipart alternative has a text and HTML
                    // version of the contents.
                    if ("plain".equalsIgnoreCase(bodyCT.getSubType())) {
                        multipartContent = (String) bodyPart.getContent();
                    }
                } else if ("mixed".equalsIgnoreCase(subType)) {
                    // Multipart mixed has a plain text part for the reply
                    // and rfc822 for the original.
                    if ("plain".equalsIgnoreCase(bodyCT.getSubType())) {
                        multipartContent = (String) bodyPart.getContent();
                    }
                } else {
                    LOGGER.info("Unhandled multipart inline content subtype {}", subType);
                }
            }
        } catch (MessagingException e) {
            LOGGER.error("Could not retrieve multipart contents", e);
        }

        return multipartContent;
    }

    private void cleanup() {
        if (inbox != null) {
            try {
                // False, because deleted messages get expunged if true.
                inbox.close(false);
            } catch (final MessagingException e) {
                LOGGER.error("Destroy exception", e);
            }
        }

        if (store != null) {
            try {
                store.close();
            } catch (final MessagingException e) {
                LOGGER.error("Destroy exception", e);
            }
        }
    }

    private void updateUI(final Iterable<RippleModel> models) {
        final DropletModel dm = new DropletModel(getIdentifier(), getUsername(), models);

        EDTUtils.runOnEDT(new Runnable() {
            @Override
            public void run() {
                view.addDropletModel(dm);
                view.dropletUpdating(false);
            }
        });
    }

}
