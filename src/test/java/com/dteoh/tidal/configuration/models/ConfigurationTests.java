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

package com.dteoh.tidal.configuration.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.After;
import org.junit.Test;

import com.dteoh.tidal.configuration.models.Configuration;

/**
 * Unit tests for the configuration model.
 * 
 * @author Douglas Teoh
 * 
 */
public class ConfigurationTests {

    private Configuration config;

    @After
    public void tearDown() {
        config = null;
    }

    @Test
    public void testGetAuthKeyDigest1() {
        config = new Configuration(null);
        assertNull(config.getAuthKeyDigest());
    }

    @Test
    public void testGetAuthKeyDigest2() {
        config = new Configuration("TESTKEY");
        assertEquals("TESTKEY", config.getAuthKeyDigest());
    }

    /**
     * Test the contract for object equals.
     */
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Configuration.class).verify();

    }

}
