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

package org.tidal_app.tidal.configuration.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the configuration model.
 * 
 * @author Douglas Teoh
 * 
 */
public class ConfigurationTests {

    private Configuration config;

    @Before
    public void setUp() {
        config = new Configuration();
    }

    @After
    public void tearDown() {
        config = null;
    }

    @Test
    public void testGetAuthKeyDigest() {
        assertNull(config.getAuthKeyDigest());
    }

    @Test
    public void testSetAuthKeyDigest() {
        config.setAuthKeyDigest("TESTKEY");
        assertEquals("TESTKEY", config.getAuthKeyDigest());
    }

}
