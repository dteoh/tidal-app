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

package org.tidal_app.tidal.sources.email;

import static org.fest.reflect.core.Reflection.field;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tidal_app.tidal.configuration.SaveConfigurable;
import org.tidal_app.tidal.exceptions.DropletCreationException;
import org.tidal_app.tidal.sources.email.impl.ImapDroplet;
import org.tidal_app.tidal.sources.email.models.EmailSettings;

/**
 * Tests for the email droplets controller.
 * 
 * @author Douglas Teoh
 * 
 */
public class EmailDropletsControllerTests {

    private EmailSettings settings;
    private EmailDropletsController controller;

    private final String hostName = "tidal-app.org";
    private final String imapProtocol = "imap";
    private final String testUser = "tester";
    private final String testPassword = "password";

    @Before
    public void setUp() {
        settings = new EmailSettings(hostName, imapProtocol, testUser,
                testPassword);
        controller = new EmailDropletsController();

        SaveConfigurable sc = mock(SaveConfigurable.class);

        field("saveConfig").ofType(SaveConfigurable.class).in(controller)
                .set(sc);
    }

    @After
    public void tearDown() {
        settings = null;
        controller = null;
    }

    /**
     * Test adding a droplet from email settings with IMAP protocol.
     */
    @Test
    public void testAddEmailDropletEmailSettings1() {
        try {
            settings = new EmailSettings(hostName, "imap", testUser,
                    testPassword);
            final AbstractEmailDroplet droplet = controller
                    .addEmailDroplet(settings);
            assertEquals(ImapDroplet.class, droplet.getClass());
        } catch (final DropletCreationException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test adding a droplet from email settings with IMAPS protocol.
     */
    @Test
    public void testAddEmailDropletEmailSettings2() {
        try {
            settings = new EmailSettings(hostName, "imaps", testUser,
                    testPassword);
            final AbstractEmailDroplet droplet = controller
                    .addEmailDroplet(settings);
            assertEquals(ImapDroplet.class, droplet.getClass());
        } catch (final DropletCreationException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test adding a droplet from email settings with unsupported protocol.
     */
    @Test
    public void testAddEmailDropletEmailSettings3() {
        try {
            settings = new EmailSettings(hostName, "unknown", testUser,
                    testPassword);
            controller.addEmailDroplet(settings);
            fail("Expecting DropletCreationException.");
        } catch (final DropletCreationException e) {
            // Test passes.
        }
    }

    /**
     * Test adding duplicate droplet from email settings.
     */
    @Test
    public void testAddEmailDropletEmailSettings4() {
        try {
            settings = new EmailSettings(hostName, "imap", testUser,
                    testPassword);
            controller.addEmailDroplet(settings);
            controller.addEmailDroplet(settings);
            fail("Expecting DropletCreationException.");
        } catch (final DropletCreationException e) {
            // Test passes.
        }
    }

    /**
     * Test adding an IMAP droplet.
     */
    @Test
    public void testAddEmailDropletAbstractEmailDroplet1() {
        try {
            final AbstractEmailDroplet droplet = ImapDroplet.create(settings);
            controller.addEmailDroplet(droplet);
        } catch (final DropletCreationException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test adding duplicate IMAP droplet.
     */
    @Test
    public void testAddEmailDropletAbstractEmailDroplet2() {
        try {
            final AbstractEmailDroplet droplet = ImapDroplet.create(settings);
            controller.addEmailDroplet(droplet);
            controller.addEmailDroplet(droplet);
            fail("Expecting DropletCreationException.");
        } catch (final DropletCreationException e) {
            // Test passed.
        }
    }

    /**
     * Test destroying a non-existent droplet.
     */
    @Test
    public void testDestroyEmailDroplet1() {
        assertFalse(controller.destroyEmailDroplet("doesn't exist"));
    }

    /**
     * Test destroying a droplet which exists.
     */
    @Test
    public void testDestroyEmailDroplet2() {
        try {
            controller.addEmailDroplet(settings);
            assertTrue(controller.destroyEmailDroplet(settings.getUsername()));
        } catch (final DropletCreationException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test destroying a droplet which exists, then re-adding the droplet.
     */
    @Test
    public void testDestroyEmailDroplet3() {
        try {
            controller.addEmailDroplet(settings);
            assertTrue(controller.destroyEmailDroplet(settings.getUsername()));
            controller.addEmailDroplet(settings);
        } catch (final DropletCreationException e) {
            fail(e.getMessage());
        }
    }

}
