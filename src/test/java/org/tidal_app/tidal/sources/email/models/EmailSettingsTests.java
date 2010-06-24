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

package org.tidal_app.tidal.sources.email.models;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for EmailSettings.
 * 
 * @author Douglas Teoh
 * 
 */
public class EmailSettingsTests {

    private EmailSettings model;

    private String host;
    private String password;
    private String protocol;
    private String username;

    @Before
    public void setUp() {
        host = "localhost";
        password = "password123";
        protocol = "imaps";
        username = "tester@tidal-app.org";

        model = new EmailSettings();
        model.setHost(host);
        model.setPassword(password);
        model.setProtocol(protocol);
        model.setUsername(username);
    }

    @After
    public void tearDown() {
        model = null;
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.sources.email.models.EmailSettings#getHost()}.
     */
    @Test
    public void testGetHost() {
        assertEquals(host, model.getHost());
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.sources.email.models.EmailSettings#setHost(java.lang.String)}
     * .
     */
    @Test
    public void testSetHost() {
        model.setHost("internet");
        assertEquals("internet", model.getHost());
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.sources.email.models.EmailSettings#getProtocol()}
     * .
     */
    @Test
    public void testGetProtocol() {
        assertEquals(protocol, model.getProtocol());
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.sources.email.models.EmailSettings#setProtocol(java.lang.String)}
     * .
     */
    @Test
    public void testSetProtocol() {
        model.setProtocol("pop3");
        assertEquals("pop3", model.getProtocol());
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.sources.email.models.EmailSettings#getUsername()}
     * .
     */
    @Test
    public void testGetUsername() {
        assertEquals(username, model.getUsername());
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.sources.email.models.EmailSettings#setUsername(java.lang.String)}
     * .
     */
    @Test
    public void testSetUsername() {
        model.setUsername("another@tidal-app.org");
        assertEquals("another@tidal-app.org", model.getUsername());
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.sources.email.models.EmailSettings#getPassword()}
     * .
     */
    @Test
    public void testGetPassword() {
        assertEquals(password, model.getPassword());
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.sources.email.models.EmailSettings#setPassword(java.lang.String)}
     * .
     */
    @Test
    public void testSetPassword() {
        model.setPassword("abc123");
        assertEquals("abc123", model.getPassword());
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.sources.email.models.EmailSettings#equals(java.lang.Object)}
     * .
     */
    @Test
    public void testEqualsObject1() {
        assertEquals(true, model.equals(model));
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.sources.email.models.EmailSettings#equals(java.lang.Object)}
     * .
     */
    @Test
    public void testEqualsObject2() {
        assertEquals(false, model.equals(null));
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.sources.email.models.EmailSettings#equals(java.lang.Object)}
     * .
     */
    @Test
    public void testEqualsObject3() {
        final EmailSettings another = model.makeCopy();

        assertEquals(true, model.equals(another));
        assertEquals(true, another.equals(model));
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.sources.email.models.EmailSettings#equals(java.lang.Object)}
     * .
     */
    @Test
    public void testEqualsObject4() {
        final EmailSettings another = model.makeCopy();
        another.setHost("newhost");

        assertEquals(false, model.equals(another));
        assertEquals(false, "newhost".equals(model.getHost()));
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.sources.email.models.EmailSettings#equals(java.lang.Object)}
     * .
     */
    @Test
    public void testEqualsObject5() {
        final EmailSettings another = model.makeCopy();
        another.setPassword("newpass");

        assertEquals(false, model.equals(another));
        assertEquals(false, "newpass".equals(model.getPassword()));
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.sources.email.models.EmailSettings#equals(java.lang.Object)}
     * .
     */
    @Test
    public void testEqualsObject6() {
        final EmailSettings another = model.makeCopy();
        another.setProtocol("newproto");

        assertEquals(false, model.equals(another));
        assertEquals(false, "newproto".equals(model.getProtocol()));
    }

    /**
     * Test method for
     * {@link org.tidal_app.tidal.sources.email.models.EmailSettings#equals(java.lang.Object)}
     * .
     */
    @Test
    public void testEqualsObject7() {
        final EmailSettings another = model.makeCopy();
        another.setUsername("newuser");

        assertEquals(false, model.equals(another));
        assertEquals(false, "newuser".equals(model.getUsername()));
    }
}
