package org.tidal_app.tidal.configuration;

import static org.fest.reflect.core.Reflection.field;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tidal_app.tidal.configuration.models.Configurable;
import org.tidal_app.tidal.configuration.models.Configuration;
import org.tidal_app.tidal.exceptions.UnsecuredException;
import org.yaml.snakeyaml.Dumper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

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

/**
 * Unit tests for the configuration controller.
 * 
 * @author Douglas Teoh
 * 
 */
public class ConfigurationControllerTests {

    private Configuration config;
    private ConfigurationController controller;

    @Before
    public void setUp() {
        config = new Configuration("digest");

        controller = new ConfigurationController();
    }

    @After
    public void tearDown() {
        config = null;
        controller = null;
    }

    /**
     * Test loading valid main program settings.
     * 
     * @throws IOException
     */
    @Test
    public void testLoadMainSettingsReader1() throws IOException {
        final PipedReader pr = new PipedReader();
        final PipedWriter pw = new PipedWriter(pr);

        final Yaml yaml = new Yaml(new Dumper(new ConfigurationRepresenter(),
                new DumperOptions()));
        yaml.dump(config, pw);
        pw.close();

        assertTrue(controller.loadMainSettings(pr));

        final Configuration controllerConfig = field("config").ofType(
                Configuration.class).in(controller).get();

        assertEquals(config, controllerConfig);

        pr.close();
    }

    /**
     * Test loading invalid main program settings. (Wrong class.)
     * 
     * @throws IOException
     */
    @Test
    public void testLoadMainSettingsReader2() throws IOException {
        final PipedReader pr = new PipedReader();
        final PipedWriter pw = new PipedWriter(pr);

        final Yaml yaml = new Yaml();
        yaml.dump("Not a valid config file", pw);
        pw.close();

        assertFalse(controller.loadMainSettings(pr));
        pr.close();
    }

    /**
     * Test loading invalid main program settings. (null authentication key)
     * 
     * @throws IOException
     */
    @Test
    public void testLoadMainSettingsReader3() throws IOException {
        final PipedReader pr = new PipedReader();
        final PipedWriter pw = new PipedWriter(pr);

        final Yaml yaml = new Yaml(new Dumper(new ConfigurationRepresenter(),
                new DumperOptions()));
        config = new Configuration(null);
        yaml.dump(config, pw);
        pw.close();

        assertFalse(controller.loadMainSettings(pr));
        pr.close();
    }

    /**
     * Test loading invalid main program settings. (empty authentication key)
     * 
     * @throws IOException
     */
    @Test
    public void testLoadMainSettingsReader4() throws IOException {
        final PipedReader pr = new PipedReader();
        final PipedWriter pw = new PipedWriter(pr);

        final Yaml yaml = new Yaml(new Dumper(new ConfigurationRepresenter(),
                new DumperOptions()));
        config = new Configuration("");
        yaml.dump(config, pw);
        pw.close();

        assertFalse(controller.loadMainSettings(pr));
        pr.close();
    }

    /**
     * Test loading invalid main program settings. (Empty file.)
     * 
     * @throws IOException
     */
    @Test
    public void testLoadMainSettingsReader5() throws IOException {
        final PipedReader pr = new PipedReader();
        final PipedWriter pw = new PipedWriter(pr);

        final Yaml yaml = new Yaml();
        yaml.dump("", pw);
        pw.close();

        assertFalse(controller.loadMainSettings(pr));
        pr.close();
    }

    /**
     * Test saving main program settings.
     * 
     * @throws UnsecuredException
     */
    @Test
    public void testSaveMainSettingsWriter1() throws UnsecuredException {
        field("config").ofType(Configuration.class).in(controller).set(config);

        final StringWriter swExpected = new StringWriter();
        final StringWriter swActual = new StringWriter();

        final Yaml yaml = new Yaml(new Dumper(new ConfigurationRepresenter(),
                new DumperOptions()));
        yaml.dump(config, swExpected);

        controller.saveMainSettings(swActual);

        assertEquals(swExpected.toString(), swActual.toString());
    }

    /**
     * Test saving main program settings when the authentication key is an empty
     * string.
     */
    @Test
    public void testSaveMainSettingsWriter2() {
        config = new Configuration("");
        field("config").ofType(Configuration.class).in(controller).set(config);

        final StringWriter swActual = new StringWriter();

        try {
            controller.saveMainSettings(swActual);
            fail("Expecting UnsecuredException.");
        } catch (final UnsecuredException e) {
            // Test passed.
        }
    }

    /**
     * Test saving main program settings when the authentication key is null.
     */
    @Test
    public void testSaveMainSettingsWriter3() {
        config = new Configuration(null);
        field("config").ofType(Configuration.class).in(controller).set(config);

        final StringWriter swActual = new StringWriter();

        try {
            controller.saveMainSettings(swActual);
            fail("Expecting UnsecuredException.");
        } catch (final UnsecuredException e) {
            // Test passed.
        }
    }

    /**
     * Test adding a configurable.
     */
    @Test
    public void testAddConfigurable1() {
        final Configurable mockConf = mock(Configurable.class);
        assertTrue(controller.addConfigurable(mockConf));
    }

    /**
     * Test adding duplicate configurables.
     */
    @Test
    public void testAddConfigurable2() {
        final Configurable mockConf = mock(Configurable.class);
        assertTrue(controller.addConfigurable(mockConf));
        assertFalse(controller.addConfigurable(mockConf));
    }

    /**
     * Test adding null configurable.
     */
    @Test
    public void testAddConfigurable3() {
        assertFalse(controller.addConfigurable(null));
    }

    /**
     * Test adding different configurables.
     */
    @Test
    public void testAddConfigurable4() {
        final Configurable mockConf1 = mock(Configurable.class);
        final Configurable mockConf2 = mock(Configurable.class);
        assertTrue(controller.addConfigurable(mockConf1));
        assertTrue(controller.addConfigurable(mockConf2));
    }

    /**
     * Test removing a configurable from empty collection.
     */
    @Test
    public void testRemoveConfigurable1() {
        final Configurable mockConf = mock(Configurable.class);
        assertFalse(controller.removeConfigurable(mockConf));
    }

    /**
     * Test removing null configurable from empty collection.
     */
    @Test
    public void testRemoveConfigurable2() {
        assertFalse(controller.removeConfigurable(null));
    }

    /**
     * Test adding and then removing same configurable.
     */
    @Test
    public void testRemoveConfigurable3() {
        final Configurable mockConf = mock(Configurable.class);
        assertTrue(controller.addConfigurable(mockConf));
        assertTrue(controller.removeConfigurable(mockConf));
    }

    /**
     * Test adding a configurable then removing null.
     */
    @Test
    public void testRemoveConfigurable4() {
        final Configurable mockConf = mock(Configurable.class);
        assertTrue(controller.addConfigurable(mockConf));
        assertFalse(controller.removeConfigurable(null));
    }

    /**
     * Test adding a configurable then removing a different configurable.
     */
    @Test
    public void testRemoveConfigurable5() {
        final Configurable mockConf1 = mock(Configurable.class);
        final Configurable mockConf2 = mock(Configurable.class);
        assertTrue(controller.addConfigurable(mockConf1));
        assertFalse(controller.removeConfigurable(mockConf2));
    }

    /**
     * Test adding and removing two different configurables. Order of removal
     * same as order of insertion.
     */
    @Test
    public void testRemoveConfigurable6() {
        final Configurable mockConf1 = mock(Configurable.class);
        final Configurable mockConf2 = mock(Configurable.class);
        assertTrue(controller.addConfigurable(mockConf1));
        assertTrue(controller.addConfigurable(mockConf2));
        assertTrue(controller.removeConfigurable(mockConf1));
        assertTrue(controller.removeConfigurable(mockConf2));
    }

    /**
     * Test adding and removing two different configurables. Order of removal is
     * different from order of insertion.
     */
    @Test
    public void testRemoveConfigurable7() {
        final Configurable mockConf1 = mock(Configurable.class);
        final Configurable mockConf2 = mock(Configurable.class);
        assertTrue(controller.addConfigurable(mockConf1));
        assertTrue(controller.addConfigurable(mockConf2));
        assertTrue(controller.removeConfigurable(mockConf2));
        assertTrue(controller.removeConfigurable(mockConf1));
    }

    /**
     * Test writing droplet settings when there are none.
     * 
     * @throws UnsecuredException
     */
    @Test
    public void testSaveDropletSettingsWriter1() throws UnsecuredException {
        field("config").ofType(Configuration.class).in(controller).set(config);

        final StringWriter sw = new StringWriter();
        controller.saveDropletSettings(sw);
        assertTrue(sw.toString().isEmpty());
    }

    /**
     * Test writing droplet settings when there is one.
     * 
     * @throws UnsecuredException
     */
    @Test
    public void testSaveDropletSettingsWriter2() throws UnsecuredException {
        field("config").ofType(Configuration.class).in(controller).set(config);

        final Configurable mockConf = mock(Configurable.class);
        when(mockConf.getSettings()).thenReturn("TESTCONF1");

        controller.addConfigurable(mockConf);

        final StringWriter swExpected = new StringWriter();
        final Yaml yaml = new Yaml();
        yaml.dump("TESTCONF1", swExpected);

        final StringWriter sw = new StringWriter();
        controller.saveDropletSettings(sw);
        assertEquals(swExpected.toString(), sw.toString());
    }

    /**
     * Test writing droplet settings when there are two.
     * 
     * @throws UnsecuredException
     */
    @Test
    public void testSaveDropletSettingsWriter3() throws UnsecuredException {
        field("config").ofType(Configuration.class).in(controller).set(config);

        final String mockConfSettings1 = "TESTCONF1";
        final String mockConfSettings2 = "TESTCONF2";

        final Configurable mockConf1 = mock(Configurable.class);
        when(mockConf1.getSettings()).thenReturn(mockConfSettings1);

        final Configurable mockConf2 = mock(Configurable.class);
        when(mockConf2.getSettings()).thenReturn(mockConfSettings2);

        controller.addConfigurable(mockConf1);
        controller.addConfigurable(mockConf2);

        final StringWriter swExpected1 = new StringWriter();
        final StringWriter swExpected2 = new StringWriter();
        final Yaml yaml = new Yaml();
        yaml.dump(mockConfSettings1, swExpected1);
        yaml.dump(mockConfSettings2, swExpected2);

        final StringWriter sw = new StringWriter();
        controller.saveDropletSettings(sw);
        assertTrue(sw.toString().contains(swExpected1.toString()));
        assertTrue(sw.toString().contains(swExpected2.toString()));
    }

    /**
     * Test writing droplet settings when there are none and empty security key
     * is set.
     */
    @Test
    public void testSaveDropletSettingsWriter4() {
        config = new Configuration("");
        field("config").ofType(Configuration.class).in(controller).set(config);

        final StringWriter sw = new StringWriter();
        try {
            controller.saveDropletSettings(sw);
            fail("Expecting UnsecuredException.");
        } catch (final UnsecuredException e) {
            // Test passed.
        }
    }

    /**
     * Test writing droplet settings when there are none and null security key
     * is set.
     */
    @Test
    public void testSaveDropletSettingsWriter5() {
        config = new Configuration(null);
        field("config").ofType(Configuration.class).in(controller).set(config);

        final StringWriter sw = new StringWriter();
        try {
            controller.saveDropletSettings(sw);
            fail("Expecting UnsecuredException.");
        } catch (final UnsecuredException e) {
            // Test passed.
        }
    }

    /**
     * Test writing droplet settings when there is one and empty security key is
     * set.
     */
    @Test
    public void testSaveDropletSettingsWriter6() {
        config = new Configuration("");
        field("config").ofType(Configuration.class).in(controller).set(config);

        final Configurable mockConf = mock(Configurable.class);
        when(mockConf.getSettings()).thenReturn("TESTCONF1");

        controller.addConfigurable(mockConf);

        final StringWriter swExpected = new StringWriter();
        final Yaml yaml = new Yaml();
        yaml.dump("TESTCONF1", swExpected);

        final StringWriter sw = new StringWriter();
        try {
            controller.saveDropletSettings(sw);
            fail("Expecting UnsecuredException.");
        } catch (final UnsecuredException e) {
            // Test passed.
        }
    }

    /**
     * Test writing droplet settings when there is one and empty security key is
     * set.
     */
    @Test
    public void testSaveDropletSettingsWriter7() {
        config = new Configuration(null);
        field("config").ofType(Configuration.class).in(controller).set(config);

        final Configurable mockConf = mock(Configurable.class);
        when(mockConf.getSettings()).thenReturn("TESTCONF1");

        controller.addConfigurable(mockConf);

        final StringWriter swExpected = new StringWriter();
        final Yaml yaml = new Yaml();
        yaml.dump("TESTCONF1", swExpected);

        final StringWriter sw = new StringWriter();
        try {
            controller.saveDropletSettings(sw);
            fail("Expecting UnsecuredException.");
        } catch (final UnsecuredException e) {
            // Test passed.
        }
    }

    /**
     * Test loading nothing.
     * 
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testLoadDropletSettingsReader1() throws IOException {
        field("config").ofType(Configuration.class).in(controller).set(config);

        final PipedReader pr = new PipedReader();
        final PipedWriter pw = new PipedWriter(pr);

        final Yaml yaml = new Yaml();
        final List empty = new LinkedList();
        yaml.dumpAll(empty.iterator(), pw);
        pw.close();

        final Iterable settings = controller.loadDropletSettings(pr);

        final Iterator it = settings.iterator();
        assertFalse(it.hasNext());

        pr.close();
    }

    /**
     * Test loading one setting.
     * 
     * @throws IOException
     */
    @Test
    public void testLoadDropletSettingsReader2() throws IOException {
        field("config").ofType(Configuration.class).in(controller).set(config);

        final PipedReader pr = new PipedReader();
        final PipedWriter pw = new PipedWriter(pr);

        final Yaml yaml = new Yaml();
        final List<String> list = new LinkedList<String>();
        list.add("TESTCONF");

        yaml.dumpAll(list.iterator(), pw);
        pw.close();

        final Iterable<Object> settings = controller.loadDropletSettings(pr);
        final Iterator<Object> it = settings.iterator();
        assertTrue(it.hasNext());
        assertEquals("TESTCONF", it.next());
        assertFalse(it.hasNext());

        pr.close();
    }

    /**
     * Test loading two settings.
     * 
     * @throws IOException
     */
    @Test
    public void testLoadDropletSettingsReader3() throws IOException {
        field("config").ofType(Configuration.class).in(controller).set(config);

        final PipedReader pr = new PipedReader();
        final PipedWriter pw = new PipedWriter(pr);

        final Yaml yaml = new Yaml();
        final List<String> list = new LinkedList<String>();
        list.add("TESTCONF1");
        list.add("TESTCONF2");

        yaml.dumpAll(list.iterator(), pw);
        pw.close();

        final Iterable<Object> settings = controller.loadDropletSettings(pr);
        final Iterator<Object> it = settings.iterator();
        assertTrue(it.hasNext());
        assertEquals("TESTCONF1", it.next());
        assertTrue(it.hasNext());
        assertEquals("TESTCONF2", it.next());
        assertFalse(it.hasNext());

        pr.close();
    }

}
