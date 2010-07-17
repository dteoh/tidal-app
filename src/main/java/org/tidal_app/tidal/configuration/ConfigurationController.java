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

import static org.tidal_app.tidal.util.EDTUtils.outsideEDT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.jasypt.util.text.StrongTextEncryptor;
import org.slf4j.Logger;
import org.tidal_app.tidal.configuration.models.Configurable;
import org.tidal_app.tidal.configuration.models.Configuration;
import org.tidal_app.tidal.exceptions.UnsecuredException;
import org.tidal_app.tidal.guice.InjectLogger;
import org.yaml.snakeyaml.Dumper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.Yaml;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * This class is responsible for managing program configuration.
 * 
 * @author Douglas Teoh
 */
public final class ConfigurationController implements SaveConfigurable {

    // TODO refactor this.

    /** Default file name for droplet settings */
    private static final String DROPLETSRC = "dropletsrc";
    /** Default file name for user settings */
    private static final String TIDALRC = "tidalrc";
    /** Default directory for Tidal user configuration settings */
    private static final String TIDAL_CONFIG_DIR = "/.tidal";
    /** Default property for retrieving user home directory */
    private static final String USER_HOME = "user.home";

    @InjectLogger
    private Logger logger;

    /** Contains master configuration information */
    private Configuration config;
    /** Symmetric key cryptography */
    private final StrongTextEncryptor encryptor;

    private final Set<Configurable> configurableInstances;
    /**
     * A state for determining if the user's configuration has been unlocked or
     * not
     */
    private boolean configurationUnlocked;

    public ConfigurationController() {
        outsideEDT();

        encryptor = new StrongTextEncryptor();
        configurableInstances = Sets.newHashSet();

        configurationUnlocked = false;
    }

    /**
     * Load the config file from a default location. If the default config file
     * doesn't exist, the current loaded config is unchanged.
     * 
     * @return true if the settings can be loaded, false otherwise.
     */
    public boolean loadMainSettings() {
        outsideEDT();

        final String homeDirectory = System.getProperty(USER_HOME);
        if (homeDirectory == null) {
            return false;
        }

        File configFile = new File(homeDirectory, TIDAL_CONFIG_DIR);
        configFile = new File(configFile, TIDALRC);
        if (!configFile.exists()) {
            return false;
        }

        boolean result = false;
        Reader fr = null;
        try {
            fr = new FileReader(configFile);
            result = loadMainSettings(fr);
        } catch (final FileNotFoundException e) {
            logger.error("Expecting file to exist.", e);
        } finally {
            IOUtils.closeQuietly(fr);
        }
        return result;
    }

    /**
     * Load main program settings from the given reader.
     * 
     * @param reader
     *            Input reader.
     * @return true if the settings can be loaded, false otherwise.
     */
    public boolean loadMainSettings(final Reader reader) {
        outsideEDT();

        boolean result = false;
        try {
            final Yaml yaml = new Yaml(new Loader(
                    new ConfigurationConstructor()));
            config = (Configuration) yaml.load(reader);
            if (config != null) {
                if (config.getAuthKeyDigest() != null
                        && !config.getAuthKeyDigest().isEmpty()) {
                    result = true;
                }
            }
        } catch (final ClassCastException e) {
            logger.error("Incorrect config file", e);
        }
        return result;
    }

    /**
     * Saves the main program's configuration settings to the default
     * configuration file. Settings are only saved if the settings are unlocked
     * by the user.
     * 
     * @throws UnsecuredException
     *             if no authorization key is set.
     */
    public void saveMainSettings() throws UnsecuredException {
        outsideEDT();

        if (!configurationUnlocked) {
            return;
        }

        final String homeDirectory = System.getProperty(USER_HOME);
        if (homeDirectory == null) {
            logger.error("No home directory");
            return;
        }

        File configFile = new File(homeDirectory, TIDAL_CONFIG_DIR);
        // mkdirs first because if the directory does not exist, then file
        // creation fails.
        configFile.mkdirs();
        configFile = new File(configFile, TIDALRC);

        Writer fw = null;
        try {
            fw = new FileWriter(configFile);
            saveMainSettings(fw);
        } catch (final IOException e) {
            logger.error("Cannot write program settings.", e);
        } finally {
            IOUtils.closeQuietly(fw);
        }
    }

    /**
     * Outputs the main configuration to the given writer.
     * 
     * @param writer
     *            Writer to output to.
     * @throws UnsecuredException
     *             if no authorization key is set.
     */
    public void saveMainSettings(final Writer writer) throws UnsecuredException {
        outsideEDT();

        if (config.getAuthKeyDigest() == null
                || config.getAuthKeyDigest().isEmpty()) {
            throw new UnsecuredException("Authorisation key cannot be blank.");
        }

        final Yaml yaml = new Yaml(new Dumper(new ConfigurationRepresenter(),
                new DumperOptions()));
        yaml.dump(config, writer);
    }

    /**
     * Saves the user's droplet settings in the default location. Settings are
     * only saved if the settings are unlocked by the user.
     * 
     * @throws UnsecuredException
     *             if no authorization key is set.
     */
    public void saveDropletSettings() throws UnsecuredException {
        if (!configurationUnlocked) {
            return;
        }

        final String homeDirectory = System.getProperty(USER_HOME);
        if (homeDirectory == null) {
            logger.error("No home directory");
            return;
        }

        File configFile = new File(homeDirectory, TIDAL_CONFIG_DIR);
        configFile.mkdirs();
        configFile = new File(configFile, DROPLETSRC);

        Writer fw = null;
        try {
            fw = new FileWriter(configFile);
            saveDropletSettings(fw);
        } catch (final IOException e) {
            logger.error("Cannot save droplet settings.", e);
        } finally {
            IOUtils.closeQuietly(fw);
        }
    }

    /**
     * Outputs all current Droplet settings to the given writer.
     * 
     * @param writer
     *            Output writer.
     * @throws UnsecuredException
     *             If no authorization key is set.
     */
    public void saveDropletSettings(final Writer writer)
            throws UnsecuredException {
        outsideEDT();

        if (config.getAuthKeyDigest() == null
                || config.getAuthKeyDigest().isEmpty()) {
            throw new UnsecuredException("Authorisation key cannot be blank.");
        }

        final Yaml yaml = new Yaml(new Dumper(new ConfigurablesRepresenter(
                encryptor), new DumperOptions()));

        final List<Object> allSettings = Lists.newLinkedList();
        for (final Configurable instance : configurableInstances) {
            allSettings.add(instance.getSettings());
        }

        yaml.dumpAll(allSettings.iterator(), writer);
    }

    /**
     * Load all droplet settings from the default droplets configuration file.
     * 
     * @return settings if the file was loaded, empty iteration otherwise.
     */
    public Iterable<Object> loadDropletSettings() {
        outsideEDT();

        final String homeDirectory = System.getProperty(USER_HOME);
        if (homeDirectory == null) {
            logger.error("No home directory");
            return Lists.newLinkedList();
        }
        File configFile = new File(homeDirectory, TIDAL_CONFIG_DIR);
        configFile = new File(configFile, DROPLETSRC);

        Iterable<Object> result = null;
        Reader fr = null;
        try {
            fr = new FileReader(configFile);
            result = loadDropletSettings(fr);
        } catch (final FileNotFoundException e) {
            logger.error("Expecting file to exist.", e);
            result = Lists.newLinkedList();
        } finally {
            IOUtils.closeQuietly(fr);
        }

        return result;
    }

    /**
     * Load all droplet settings from the given file.
     * 
     * @param reader
     * @return droplet settings if the file was loaded, {@code null} otherwise.
     */
    public Iterable<Object> loadDropletSettings(final Reader reader) {
        outsideEDT();

        Iterable<Object> allSettings = null;

        final Yaml yaml = new Yaml(new Loader(new ConfigurablesConstructor(
                encryptor)));
        allSettings = yaml.loadAll(reader);

        return allSettings;
    }

    /**
     * Unlock the configuration in use.
     * 
     * @param authKey
     * @return true if authorized, false otherwise.
     */
    public boolean authorize(final String authKey) {
        outsideEDT();

        final StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
        if (passwordEncryptor.checkPassword(authKey, config.getAuthKeyDigest())) {
            encryptor.setPassword(authKey);
            configurationUnlocked = true;
            return true;
        }
        return false;
    }

    /**
     * Changes the authorization key.
     * 
     * @param newAuthKey
     * @throws UnsecuredException
     *             if no authorization key is set.
     */
    public void changeAuthorizationKey(final String newAuthKey)
            throws UnsecuredException {
        outsideEDT();

        if (newAuthKey == null || newAuthKey.isEmpty()) {
            throw new UnsecuredException("Authorisation key cannot be blank.");
        }

        final StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
        config = new Configuration(
                passwordEncryptor.encryptPassword(newAuthKey));

        encryptor.setPassword(newAuthKey);

        configurationUnlocked = true;
    }

    /**
     * Add a Configurable object for tracking by the controller. The object is
     * assumed to be a droplet, as such, the object will be serialized to the
     * droplet settings file.
     * 
     * @param instance
     *            The object to track.
     * @return true if the object is being tracked. false if the object is null
     *         or if the object is already being tracked (some other object is
     *         equal to the given object).
     */
    public boolean addConfigurable(final Configurable instance) {
        outsideEDT();

        if (instance == null) {
            return false;
        }

        if (configurableInstances.contains(instance)) {
            return false;
        }

        configurableInstances.add(instance);
        return true;
    }

    /**
     * Remove a Configurable object from the controller.
     * 
     * @param instance
     *            The object to remove.
     * @return true if the object was removed. false if the given object is null
     *         or if it is not being tracked by the controller.
     */
    public boolean removeConfigurable(final Configurable instance) {
        outsideEDT();

        if (instance == null) {
            return false;
        }

        if (!configurableInstances.contains(instance)) {
            return false;
        }
        configurableInstances.remove(instance);
        return true;
    }
}
