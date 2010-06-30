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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testGetHost() {
        assertEquals(host, model.getHost());
    }

    @Test
    public void testSetHost() {
        model.setHost("internet");
        assertEquals("internet", model.getHost());
    }

    @Test
    public void testGetProtocol() {
        assertEquals(protocol, model.getProtocol());
    }

    @Test
    public void testSetProtocol() {
        model.setProtocol("pop3");
        assertEquals("pop3", model.getProtocol());
    }

    @Test
    public void testGetUsername() {
        assertEquals(username, model.getUsername());
    }

    @Test
    public void testSetUsername() {
        model.setUsername("another@tidal-app.org");
        assertEquals("another@tidal-app.org", model.getUsername());
    }

    @Test
    public void testGetPassword() {
        assertEquals(password, model.getPassword());
    }

    @Test
    public void testSetPassword() {
        model.setPassword("abc123");
        assertEquals("abc123", model.getPassword());
    }

    /**
     * Reflexive property.
     */
    @Test
    public void testEqualsObject1() {
        assertTrue(model.equals(model));
    }

    /**
     * Transitive property.
     */
    @Test
    public void testEqualsObject2() {
        final EmailSettings model2 = new EmailSettings();
        model2.setHost(host);
        model2.setPassword(password);
        model2.setProtocol(protocol);
        model2.setUsername(username);

        final EmailSettings model3 = new EmailSettings();
        model3.setHost(host);
        model3.setPassword(password);
        model3.setProtocol(protocol);
        model3.setUsername(username);

        assertTrue(model.equals(model2));
        assertTrue(model2.equals(model3));
        assertTrue(model.equals(model3));
    }

    /**
     * Reflexive property.
     */
    @Test
    public void testEqualsObject3() {
        final EmailSettings another = model.makeCopy();

        assertTrue(model.equals(another));
        assertTrue(another.equals(model));
    }

    /**
     * Inequality with null.
     */
    @Test
    public void testEqualsObject4() {
        assertFalse(model.equals(null));
    }

    /**
     * Incompatible class with null.
     */
    @Test
    public void testEqualsObject5() {
        assertFalse(model.equals("string"));
    }

    /**
     * Different host.
     */
    @Test
    public void testEqualsObject6() {
        final EmailSettings another = model.makeCopy();
        another.setHost("newhost");

        assertFalse(model.equals(another));
        assertFalse("newhost".equals(model.getHost()));
    }

    /**
     * Null host.
     */
    @Test
    public void testEqualsObject7() {
        final EmailSettings another = model.makeCopy();
        another.setHost(null);

        assertFalse(model.equals(another));
    }

    /**
     * Different password.
     */
    @Test
    public void testEqualsObject8() {
        final EmailSettings another = model.makeCopy();
        another.setPassword("newpass");

        assertFalse(model.equals(another));
        assertFalse("newpass".equals(model.getPassword()));
    }

    /**
     * null password.
     */
    @Test
    public void testEqualsObject9() {
        final EmailSettings another = model.makeCopy();
        another.setPassword(null);

        assertFalse(model.equals(another));
    }

    /**
     * Different protocol.
     */
    @Test
    public void testEqualsObject10() {
        final EmailSettings another = model.makeCopy();
        another.setProtocol("newproto");

        assertFalse(model.equals(another));
        assertFalse("newproto".equals(model.getProtocol()));
    }

    /**
     * Null protocol.
     */
    @Test
    public void testEqualsObject11() {
        final EmailSettings another = model.makeCopy();
        another.setProtocol(null);

        assertFalse(model.equals(another));
    }

    /**
     * Different username.
     */
    @Test
    public void testEqualsObject12() {
        final EmailSettings another = model.makeCopy();
        another.setUsername("newuser");

        assertFalse(model.equals(another));
        assertFalse("newuser".equals(model.getUsername()));
    }

    /**
     * Null username.
     */
    @Test
    public void testEqualsObject13() {
        final EmailSettings another = model.makeCopy();
        another.setUsername(null);

        assertFalse(model.equals(another));
    }
}
