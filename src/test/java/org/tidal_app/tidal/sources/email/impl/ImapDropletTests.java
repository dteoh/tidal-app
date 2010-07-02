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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.mock_javamail.Mailbox;
import org.tidal_app.tidal.exceptions.DropletCreationException;
import org.tidal_app.tidal.exceptions.DropletInitException;
import org.tidal_app.tidal.sources.email.models.EmailRipple;
import org.tidal_app.tidal.sources.email.models.EmailSettings;

public class ImapDropletTests {

    private EmailSettings settings;

    private final String hostName = "tidal-app.org";
    private final String imapProtocol = "imap";
    private final String testUser = "tester";
    private final String testPassword = "password";

    @Before
    public void setUp() {
        settings = new EmailSettings(hostName, imapProtocol, testUser,
                testPassword);
    }

    @After
    public void tearDown() {
        settings = null;
        Mailbox.clearAll();
    }

    /**
     * Test creating droplet from EmailSettings.
     */
    @Test
    public void testCreate1() {
        try {
            final ImapDroplet droplet = ImapDroplet.create(settings);
            assertEquals(settings, droplet.getSettings());
        } catch (final DropletCreationException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test creating droplet from EmailSettings attributes.
     */
    @Test
    public void testCreate2() {
        try {
            final ImapDroplet droplet = ImapDroplet.create(settings.getHost(),
                    settings.getProtocol(), settings.getUsername(), settings
                            .getPassword());
            assertEquals(settings, droplet.getSettings());
        } catch (final DropletCreationException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test support for "imap" protocol.
     */
    @Test
    public void testCreate3() {
        try {
            ImapDroplet.create(settings.getHost(), "imap", settings
                    .getUsername(), settings.getPassword());
        } catch (final DropletCreationException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test support for "imaps" protocol.
     */
    @Test
    public void testCreate4() {
        try {
            ImapDroplet.create(settings.getHost(), "imaps", settings
                    .getUsername(), settings.getPassword());
        } catch (final DropletCreationException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test support for other protocols.
     */
    @Test
    public void testCreate5() {
        try {
            ImapDroplet.create(settings.getHost(), "pop3", settings
                    .getUsername(), settings.getPassword());
            fail("Expecting DropletCreationException.");
        } catch (final DropletCreationException e) {
            // Pass.
        }
    }

    /**
     * Test ability to initialize connection to mailbox.
     */
    @Test
    public void testInit() {
        ImapDroplet droplet = null;
        try {
            droplet = ImapDroplet.create(settings);
        } catch (final DropletCreationException e) {
            fail(e.getMessage());
        }
        try {
            droplet.init();
        } catch (final DropletInitException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test retrieving ripples when there are none.
     */
    @Test
    public void testGetRipples1() {
        ImapDroplet droplet = null;
        try {
            droplet = ImapDroplet.create(settings);
            droplet.init();
        } catch (final Exception e) {
            fail(e.getMessage());
        }
        final Iterable<EmailRipple> ripples = droplet.getRipples();
        assertFalse(ripples.iterator().hasNext());
    }

    /**
     * Test retrieving ripples when there is one.
     */
    @Test
    public void testGetRipples2() {
        final Properties props = System.getProperties();
        final Session session = Session.getInstance(props, null);

        try {
            final MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("unittests@tidal-app.org"));
            msg.setRecipients(Message.RecipientType.TO, "tester@tidal-app.org");
            msg.setSentDate(Calendar.getInstance().getTime());
            msg.setSubject("Unit test email");
            msg.setContent("Unit test email for testGetRipples2", "text/plain");
            Transport.send(msg);
        } catch (final Exception e) {
            fail(e.getMessage());
        }

        ImapDroplet droplet = null;
        try {
            droplet = ImapDroplet.create(settings);
            droplet.init();
        } catch (final Exception e) {
            fail(e.getMessage());
        }
        final Iterable<EmailRipple> ripples = droplet.getRipples();
        final Iterator<EmailRipple> it = ripples.iterator();
        assertTrue(it.hasNext());
        it.next();
        assertFalse(it.hasNext());
    }

    /**
     * Test retrieving ripples when there are two.
     */
    @Test
    public void testGetRipples3() {
        final Properties props = System.getProperties();
        final Session session = Session.getInstance(props, null);

        try {
            final MimeMessage m1 = new MimeMessage(session);
            m1.setFrom(new InternetAddress("unittests@tidal-app.org"));
            m1.setRecipients(Message.RecipientType.TO, "tester@tidal-app.org");
            m1.setSentDate(Calendar.getInstance().getTime());
            m1.setSubject("Unit test email 1");
            m1.setContent("Unit test email for testGetRipples3", "text/plain");
            Transport.send(m1);

            final MimeMessage m2 = new MimeMessage(session);
            m2.setFrom(new InternetAddress("unittests@tidal-app.org"));
            m2.setRecipients(Message.RecipientType.TO, "tester@tidal-app.org");
            m2.setSentDate(Calendar.getInstance().getTime());
            m2.setSubject("Unit test email 2");
            m2.setContent("Unit test email for testGetRipples3", "text/plain");
            Transport.send(m2);
        } catch (final Exception e) {
            fail(e.getMessage());
        }

        ImapDroplet droplet = null;
        try {
            droplet = ImapDroplet.create(settings);
            droplet.init();
        } catch (final Exception e) {
            fail(e.getMessage());
        }
        final Iterable<EmailRipple> ripples = droplet.getRipples();
        final Iterator<EmailRipple> it = ripples.iterator();
        assertTrue(it.hasNext());
        it.next();
        assertTrue(it.hasNext());
        it.next();
        assertFalse(it.hasNext());
    }

    /**
     * Test retrieving ripples one after another. I.e. send one email, retrieve
     * it, send another, retrieve both.
     */
    @Test
    public void testGetRipples4() {
        final Properties props = System.getProperties();
        final Session session = Session.getInstance(props, null);

        try {
            final MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("unittests@tidal-app.org"));
            msg.setRecipients(Message.RecipientType.TO, "tester@tidal-app.org");
            msg.setSentDate(Calendar.getInstance().getTime());
            msg.setSubject("Unit test email 1");
            msg.setContent("Unit test email for testGetRipples4", "text/plain");
            Transport.send(msg);
        } catch (final Exception e) {
            fail(e.getMessage());
        }

        ImapDroplet droplet = null;
        try {
            droplet = ImapDroplet.create(settings);
            droplet.init();
        } catch (final Exception e) {
            fail(e.getMessage());
        }
        Iterable<EmailRipple> ripples = droplet.getRipples();
        Iterator<EmailRipple> it = ripples.iterator();
        assertTrue(it.hasNext());
        it.next();
        assertFalse(it.hasNext());

        try {
            final MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("unittests@tidal-app.org"));
            msg.setRecipients(Message.RecipientType.TO, "tester@tidal-app.org");
            msg.setSentDate(Calendar.getInstance().getTime());
            msg.setSubject("Unit test email 2");
            msg.setContent("Unit test email for testGetRipples4", "text/plain");
            Transport.send(msg);
        } catch (final Exception e) {
            fail(e.getMessage());
        }

        ripples = droplet.getRipples();
        it = ripples.iterator();
        assertTrue(it.hasNext());
        it.next();
        assertTrue(it.hasNext());
        it.next();
        assertFalse(it.hasNext());
    }

    /**
     * Test retrieving ripples contents.
     */
    @Test
    public void testGetRipples5() {
        final Properties props = System.getProperties();
        final Session session = Session.getInstance(props, null);

        final String fromAddr = "unittests@tidal-app.org";
        final String toAddr = "tester@tidal-app.org";
        final String subject = "testGetRipples5";
        final String contents = "Unit test email for testGetRipples5.";
        final long time = 150000;

        try {
            final MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromAddr));
            msg.setRecipients(Message.RecipientType.TO, toAddr);
            final Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            msg.setSentDate(cal.getTime());
            msg.setSubject(subject);
            msg.setContent(contents, "text/plain");
            Transport.send(msg);
        } catch (final Exception e) {
            fail(e.getMessage());
        }

        ImapDroplet droplet = null;
        try {
            droplet = ImapDroplet.create(settings);
            droplet.init();
        } catch (final Exception e) {
            fail(e.getMessage());
        }
        final Iterable<EmailRipple> ripples = droplet.getRipples();
        final Iterator<EmailRipple> it = ripples.iterator();
        assertTrue(it.hasNext());

        final EmailRipple ripple = it.next();
        assertEquals(fromAddr, ripple.getSender());
        assertEquals(subject, ripple.getSubject());
        assertEquals(contents, ripple.getContent());
        assertEquals(time, ripple.getEpochSentTime());

        assertFalse(it.hasNext());
    }
}
