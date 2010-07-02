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

package org.tidal_app.tidal.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;

import org.jasypt.util.text.StrongTextEncryptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tidal_app.tidal.sources.email.models.EmailSettings;
import org.yaml.snakeyaml.Dumper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.Yaml;

/**
 * Unit tests for serializing and deserializing droplet settings.
 * 
 * @author Douglas Teoh
 * 
 */
public class ConfigurablesYamlTests {

    private Yaml yamlConstruct;
    private Yaml yamlRepresent;
    private StrongTextEncryptor enc;
    private final String password = "P@SSV\\/0RD.";

    @Before
    public void setUp() {
        enc = new StrongTextEncryptor();
        enc.setPassword(password);
        yamlConstruct = new Yaml(new Loader(new ConfigurablesConstructor(enc)));
        yamlRepresent = new Yaml(new Dumper(new ConfigurablesRepresenter(enc),
                new DumperOptions()));
    }

    @After
    public void tearDown() {
        yamlConstruct = null;
        yamlRepresent = null;
        enc = null;
    }

    /**
     * Test serialization of EmailSettings object.
     */
    @Test
    public void testEmailSettings1() {
        final String host = "tidal-app.org";
        final String protocol = "imap";
        final String username = "unittester";
        final String password = "junittesting";
        final EmailSettings es = new EmailSettings(host, protocol, username,
                password);

        final StringWriter sw = new StringWriter();

        yamlRepresent.dump(es, sw);

        final String result = sw.toString();

        // Check for tag
        assertTrue(result.contains("!email"));

        // Check for field names
        assertTrue(result.contains("host"));
        assertTrue(result.contains("prot"));
        assertTrue(result.contains("user"));
        assertTrue(result.contains("pass"));

        // Check for field values
        assertTrue(result.contains(host));
        assertTrue(result.contains(protocol));
        assertFalse(result.contains(username));
        assertFalse(result.contains(password));
    }

    /**
     * Test deserialization of EmailSettings object.
     */
    @Test
    public void testEmailSettings2() {
        final String host = "tidal-app.org";
        final String protocol = "imap";
        final String username = "unittester";
        final String password = "junittesting";
        final EmailSettings es = new EmailSettings(host, protocol, username,
                password);

        final StringWriter sw = new StringWriter();

        yamlRepresent.dump(es, sw);

        final EmailSettings result = (EmailSettings) yamlConstruct.load(sw
                .toString());
        assertEquals(es, result);
    }

    /**
     * Test deserialization of invalid EmailSettings object.
     */
    @Test
    public void testEmailSettings3() {
        final String host = null;
        final String protocol = "imap";
        final String username = "unittester";
        final String password = "junittesting";
        final EmailSettings es = new EmailSettings(host, protocol, username,
                password);

        final StringWriter sw = new StringWriter();

        yamlRepresent.dump(es, sw);

        final EmailSettings result = (EmailSettings) yamlConstruct.load(sw
                .toString());
        assertNull(result);
    }

    /**
     * Test deserialization of invalid EmailSettings object.
     */
    @Test
    public void testEmailSettings4() {
        final String host = "tidal-app.org";
        final String protocol = null;
        final String username = "unittester";
        final String password = "junittesting";
        final EmailSettings es = new EmailSettings(host, protocol, username,
                password);

        final StringWriter sw = new StringWriter();

        yamlRepresent.dump(es, sw);

        final EmailSettings result = (EmailSettings) yamlConstruct.load(sw
                .toString());
        assertNull(result);
    }

    /**
     * Test deserialization of invalid EmailSettings object.
     */
    @Test
    public void testEmailSettings5() {
        final String host = "tidal-app.org";
        final String protocol = "imap";
        final String username = null;
        final String password = "junittesting";
        final EmailSettings es = new EmailSettings(host, protocol, username,
                password);

        final StringWriter sw = new StringWriter();

        yamlRepresent.dump(es, sw);

        final EmailSettings result = (EmailSettings) yamlConstruct.load(sw
                .toString());
        assertNull(result);
    }

    /**
     * Test deserialization of invalid EmailSettings object.
     */
    @Test
    public void testEmailSettings6() {
        final String host = "tidal-app.org";
        final String protocol = "imap";
        final String username = "unittester";
        final String password = null;
        final EmailSettings es = new EmailSettings(host, protocol, username,
                password);

        final StringWriter sw = new StringWriter();

        yamlRepresent.dump(es, sw);

        final EmailSettings result = (EmailSettings) yamlConstruct.load(sw
                .toString());
        assertNull(result);
    }
}
