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

package org.tidal_app.tidal.sources.email.impl;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tidal_app.tidal.exceptions.DropletCreationException;
import org.tidal_app.tidal.exceptions.DropletInitException;
import org.tidal_app.tidal.sources.email.AbstractEmailDroplet;
import org.tidal_app.tidal.sources.email.models.EmailRipple;
import org.tidal_app.tidal.sources.email.models.EmailSettings;

/**
 * This Droplet is used to handle IMAP/IMAPS email services.
 * 
 * @author Douglas Teoh
 */
public final class ImapDroplet extends AbstractEmailDroplet {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ImapDroplet.class);

    private Store store = null;
    private Folder inbox = null;

    public static ImapDroplet create(final EmailSettings settings)
            throws DropletCreationException {
        assert (!SwingUtilities.isEventDispatchThread());

        final String protocol = settings.getProtocol();
        if (!"imap".equals(protocol) && !"imaps".equals(protocol)) {
            throw new DropletCreationException("Unsupported protocol: "
                    + protocol);
        }
        return new ImapDroplet(settings);
    }

    public static ImapDroplet create(final String host, final String protocol,
            final String username, final String password)
            throws DropletCreationException {
        assert (!SwingUtilities.isEventDispatchThread());

        if (!"imap".equals(protocol) && !"imaps".equals(protocol)) {
            throw new DropletCreationException("Unsupported protocol: "
                    + protocol);
        }
        return new ImapDroplet(host, protocol, username, password);
    }

    private ImapDroplet(final EmailSettings settings) {
        super(settings);
    }

    /**
     * @param host
     * @param protocol
     *            must be either IMAP or IMAPS
     * @param username
     * @param password
     */
    private ImapDroplet(final String host, final String protocol,
            final String username, final String password) {
        super(host, protocol, username, password);
    }

    @Override
    public void destroy() {
        assert (!SwingUtilities.isEventDispatchThread());

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

    @Override
    public void init() throws DropletInitException {
        assert (!SwingUtilities.isEventDispatchThread());

        final Properties props = System.getProperties();
        final Session session = Session.getInstance(props, null);

        // Set up the mailbox to read from
        try {
            store = session.getStore(settings.getProtocol());
            store.connect(settings.getHost(), settings.getUsername(), settings
                    .getPassword());
            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
        } catch (final NoSuchProviderException e) {
            LOGGER.error("Init exception", e);
            throw new DropletInitException(e);
        } catch (final MessagingException e) {
            LOGGER.error("Init exception", e);
            throw new DropletInitException(e);
        }
    }

    @Override
    public Iterable<EmailRipple> getRipples() {
        assert (!SwingUtilities.isEventDispatchThread());

        if (inbox == null) {
            return null;
        }
        try {
            // This search option will only download unread messages.
            final FlagTerm searchOption = new FlagTerm(new Flags(
                    Flags.Flag.SEEN), false);
            final Message[] messages = inbox.search(searchOption);

            // Make ripples
            final List<EmailRipple> unreadRipples = new LinkedList<EmailRipple>();
            for (int i = 0; i < messages.length; i++) {
                final Address[] senderAddresses = messages[i].getFrom();
                final String subject = messages[i].getSubject();
                final Date received = messages[i].getReceivedDate();

                final String contentType = messages[i].getContentType();
                String content = "Only plaintext and HTML emails are supported.";
                if ("text/plain".equalsIgnoreCase(contentType)
                        || "text/html".equalsIgnoreCase(contentType)) {
                    content = (String) messages[i].getContent();
                }

                unreadRipples.add(new EmailRipple(messages[i]
                        .getMessageNumber(),
                        senderAddresses.length > 0 ? senderAddresses[0]
                                .toString() : "Unknown", subject, content,
                        received.getTime()));
            }
            return unreadRipples;
        } catch (final MessagingException e) {
            LOGGER.error("Could not download messages", e);
        } catch (final IOException e) {
            LOGGER.error("Could not download message content", e);
        }
        return null;
    }
}
