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

package com.dteoh.tidal.sources.email;

import static org.fest.reflect.core.Reflection.constructor;
import static org.fest.reflect.core.Reflection.field;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dteoh.tidal.configuration.SaveConfigurable;
import com.dteoh.tidal.controllers.ViewManager;
import com.dteoh.tidal.exceptions.DropletCreationException;
import com.dteoh.tidal.id.ID;
import com.dteoh.tidal.sources.email.AbstractEmailDroplet;
import com.dteoh.tidal.sources.email.EmailDropletsController;
import com.dteoh.tidal.sources.email.impl.ImapDroplet;
import com.dteoh.tidal.sources.email.models.EmailSettings;
import com.dteoh.tidal.sources.email.models.Protocol;

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
    private final Protocol imapProtocol = Protocol.imap;
    private final String testUser = "tester";
    private final String testPassword = "password";

    @Before
    public void setUp() {
        settings = new EmailSettings(hostName, imapProtocol, testUser,
                testPassword);
        controller = constructor().in(EmailDropletsController.class)
                .newInstance();

        SaveConfigurable sc = mock(SaveConfigurable.class);
        ViewManager dv = mock(ViewManager.class);

        field("saveConfig").ofType(SaveConfigurable.class).in(controller)
                .set(sc);
        field("viewManager").ofType(ViewManager.class).in(controller).set(dv);
    }

    @After
    public void tearDown() {
        settings = null;
        controller = null;
    }

    /**
     * Test adding a droplet from email settings with imap protocol.
     */
    @Test
    public void testAddEmailDropletEmailSettings1() {
        try {
            settings = new EmailSettings(hostName, Protocol.imap, testUser,
                    testPassword);
            final AbstractEmailDroplet droplet = controller
                    .addEmailDroplet(settings);
            assertEquals(ImapDroplet.class, droplet.getClass());
        } catch (final DropletCreationException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test adding a droplet from email settings with imaps protocol.
     */
    @Test
    public void testAddEmailDropletEmailSettings2() {
        try {
            settings = new EmailSettings(hostName, Protocol.imaps, testUser,
                    testPassword);
            final AbstractEmailDroplet droplet = controller
                    .addEmailDroplet(settings);
            assertEquals(ImapDroplet.class, droplet.getClass());
        } catch (final DropletCreationException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test adding duplicate droplet from email settings.
     */
    @Test
    public void testAddEmailDropletEmailSettings3() {
        try {
            settings = new EmailSettings(hostName, Protocol.imap, testUser,
                    testPassword);
            controller.addEmailDroplet(settings);
            controller.addEmailDroplet(settings);
        } catch (final DropletCreationException e) {
            fail("Duplicate droplets allowed.");
        }
    }

    /**
     * Test destroying a non-existent droplet.
     */
    @Test
    public void testDestroyEmailDroplet1() {
        assertFalse(controller.destroyDroplet(mock(ID.class)));
    }

    /**
     * Test destroying a droplet which exists.
     */
    @Test
    public void testDestroyEmailDroplet2() {
        try {
            AbstractEmailDroplet d = controller.addEmailDroplet(settings);
            assertTrue(controller.destroyDroplet(d.getIdentifier()));
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
            AbstractEmailDroplet d = controller.addEmailDroplet(settings);
            assertTrue(controller.destroyDroplet(d.getIdentifier()));
            controller.addEmailDroplet(settings);
        } catch (final DropletCreationException e) {
            fail(e.getMessage());
        }
    }

}
