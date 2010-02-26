package org.tidal_app.tidal.impl.sources.gmail;

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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tidal_app.tidal.exceptions.DropletInitException;
import org.tidal_app.tidal.sources.EmailDroplet;
import org.tidal_app.tidal.sources.models.EmailRipple;

public class GmailDroplet extends EmailDroplet {

    private static Logger LOGGER = LoggerFactory.getLogger(GmailDroplet.class);

    private static String HOST = "imap.gmail.com";
    private static String PROTOCOL = "imaps";

    private Session session = null;
    private Store store = null;
    private Folder inbox = null;

    public GmailDroplet(final String username, final String password) {
        super(username, password);
    }

    @Override
    public void destroy() {
        if (inbox != null) {
            try {
                // False, because deleted messages get expunged if true.
                inbox.close(false);
            } catch (MessagingException e) {
                LOGGER.error("Destroy exception", e);
            }
        }

        if (store != null) {
            try {
                store.close();
            } catch (MessagingException e) {
                LOGGER.error("Destroy exception", e);
            }
        }
    }

    @Override
    public void init() throws DropletInitException {
        Properties props = System.getProperties();
        session = Session.getInstance(props, null);

        // Set up the mailbox to read from
        try {
            store = session.getStore(PROTOCOL);
            store.connect(HOST, username, password);
            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
        } catch (NoSuchProviderException e) {
            LOGGER.error("Init exception", e);
            throw new DropletInitException(e.getCause());
        } catch (MessagingException e) {
            LOGGER.error("Init exception", e);
            throw new DropletInitException(e.getCause());
        }
    }

    @Override
    public List<EmailRipple> getRipples() {
        if (inbox == null) {
            return null;
        }
        try {
            // This search option will only download unread messages.
            FlagTerm searchOption =
                new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            Message[] messages = inbox.search(searchOption);

            // Make ripples
            List<EmailRipple> unreadRipples = new LinkedList<EmailRipple>();
            for (int i = 0; i < messages.length; i++) {
                Address[] senderAddresses = messages[i].getFrom();
                String subject = messages[i].getSubject();
                Date received = messages[i].getReceivedDate();

                String contentType = messages[i].getContentType();
                String content =
                    "Only plaintext and HTML emails are supported.";
                if (contentType.equalsIgnoreCase("text/plain")
                    || contentType.equalsIgnoreCase("text/html")) {
                    content = (String) messages[i].getContent();
                }

                unreadRipples.add(new EmailRipple(messages[i]
                        .getMessageNumber(), senderAddresses.length > 0
                    ? senderAddresses[0].toString() : "Unknown", subject,
                        content, received.getTime()));
            }
            return unreadRipples;
        } catch (MessagingException e) {
            LOGGER.error("Could not download messages", e);
        } catch (IOException e) {
            LOGGER.error("Could not download message content", e);
        }
        return null;
    }
}
