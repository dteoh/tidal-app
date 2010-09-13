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
import nl.jqno.equalsverifier.EqualsVerifier;

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
    private Protocol protocol;
    private String username;

    @Before
    public void setUp() {
        host = "localhost";
        password = "password123";
        protocol = Protocol.imaps;
        username = "tester@tidal-app.org";

        model = new EmailSettings(host, protocol, username, password);
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
    public void testGetProtocol() {
        assertEquals(protocol, model.getProtocol());
    }

    @Test
    public void testGetUsername() {
        assertEquals(username, model.getUsername());
    }

    @Test
    public void testGetPassword() {
        assertEquals(password, model.getPassword());
    }

    /**
     * Reflexive property.
     */
    @Test
    public void testEqualsContract() {
        EqualsVerifier.forClass(EmailSettings.class).verify();
    }

}
