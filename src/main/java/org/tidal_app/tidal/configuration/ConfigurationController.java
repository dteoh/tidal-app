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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.apache.commons.io.IOUtils;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.jasypt.util.text.StrongTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tidal_app.tidal.configuration.models.Configuration;
import org.tidal_app.tidal.exceptions.UnsecuredException;
import org.yaml.snakeyaml.Dumper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.Yaml;

/**
 * This class is responsible for managing program configuration.
 * 
 * @author Douglas Teoh
 */
public class ConfigurationController {

    /** Default file name for droplet settings */
    private static final String DROPLETSRC = "dropletsrc";
    /** Default file name for user settings */
    private static final String TIDALRC = "tidalrc";
    /** Default directory for Tidal user configuration settings */
    private static final String TIDAL_CONFIG_DIR = "/.tidal";
    /** Default property for retrieving user home directory */
    private static final String USER_HOME = "user.home";

    private final static Logger LOGGER = LoggerFactory
            .getLogger(ConfigurationController.class);

    /** Contains master configuration information */
    private transient Configuration config;
    /** Symmetric key cryptography */
    private final StrongTextEncryptor encryptor;

    private final Set<Configurable> configurableInstances;
    /**
     * A state for determining if the user's configuration has been unlocked or
     * not
     */
    private boolean configurationUnlocked;

    public ConfigurationController() {
        assert (!SwingUtilities.isEventDispatchThread());

        config = new Configuration();
        encryptor = new StrongTextEncryptor();
        configurableInstances = new HashSet<Configurable>();

        configurationUnlocked = false;
    }

    /**
     * Load the config file from a default location. If the default config file
     * doesn't exist, the current loaded config is unchanged.
     * 
     * @return true if the settings can be loaded, false otherwise.
     */
    public boolean loadMainSettings() {
        assert (!SwingUtilities.isEventDispatchThread());

        final String homeDirectory = System.getProperty(USER_HOME);
        if (homeDirectory == null) {
            return false;
        }
        File configFile = new File(homeDirectory, TIDAL_CONFIG_DIR);
        configFile = new File(configFile, TIDALRC);
        if (!configFile.exists()) {
            return false;
        }
        return loadMainSettings(configFile);
    }

    /**
     * Load the given config file. If the file doesn't exist, the current loaded
     * config is unchanged.
     * 
     * @param file
     * @return true if the settings can be loaded, false otherwise.
     */
    public boolean loadMainSettings(final File file) {
        assert (!SwingUtilities.isEventDispatchThread());

        boolean result = false;
        FileReader fr = null;
        try {
            fr = new FileReader(file);
            final Yaml yaml = new Yaml();
            config = (Configuration) yaml.load(fr);
            if (config != null) {
                result = true;
            }
        } catch (final FileNotFoundException e) {
            LOGGER.error("File not found", e);
        } catch (final ClassCastException e) {
            LOGGER.error("Incorrect config file", e);
        } finally {
            IOUtils.closeQuietly(fr);
        }
        return result;
    }

    /**
     * Saves the main program's configuration settings to the default
     * configuration file.
     * 
     * @throws UnsecuredException
     *             if no authorization key is set.
     */
    public void saveMainSettings() throws UnsecuredException {
        assert (!SwingUtilities.isEventDispatchThread());

        if (!configurationUnlocked) {
            return;
        }

        final String homeDirectory = System.getProperty(USER_HOME);
        if (homeDirectory == null) {
            LOGGER.error("No home directory");
            return;
        }
        File configFile = new File(homeDirectory, TIDAL_CONFIG_DIR);
        configFile = new File(configFile, TIDALRC);
        saveMainSettings(configFile);
    }

    /**
     * Save the configuration file to the given file path.
     * 
     * @param file
     * @throws UnsecuredException
     *             if no authorization key is set.
     */
    public void saveMainSettings(final File file) throws UnsecuredException {
        assert (!SwingUtilities.isEventDispatchThread());

        if (!configurationUnlocked) {
            return;
        }

        if (config.getAuthKeyDigest().isEmpty()
                || config.getAuthKeyDigest() == null) {
            throw new UnsecuredException("Authorisation key cannot be blank.");
        }

        FileWriter fw = null;
        try {
            // mkdir first because if the directory does not exist, then file
            // creation fails.
            file.getParentFile().mkdir();
            fw = new FileWriter(file);
            final Yaml yaml = new Yaml();
            yaml.dump(config, fw);
        } catch (final IOException e) {
            LOGGER.error("Cannot write config", e);
        } finally {
            IOUtils.closeQuietly(fw);
        }
    }

    /**
     * Saves the user's droplet settings in the default location.
     * 
     * @throws UnsecuredException
     */
    public void saveDropletSettings() throws UnsecuredException {
        if (!configurationUnlocked) {
            return;
        }

        final String homeDirectory = System.getProperty(USER_HOME);
        if (homeDirectory == null) {
            LOGGER.error("No home directory");
            return;
        }
        File configFile = new File(homeDirectory, TIDAL_CONFIG_DIR);
        configFile = new File(configFile, DROPLETSRC);
        saveDropletSettings(configFile);
    }

    /**
     * Saves all current Droplet settings to the given file.
     * 
     * @param file
     * @throws UnsecuredException
     *             if there is no authorization key
     */
    public void saveDropletSettings(final File file) throws UnsecuredException {
        assert (!SwingUtilities.isEventDispatchThread());

        if (!configurationUnlocked) {
            return;
        }

        if (config.getAuthKeyDigest().isEmpty()
                || config.getAuthKeyDigest() == null) {
            throw new UnsecuredException("Authorisation key cannot be blank.");
        }

        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            final Yaml yaml = new Yaml(new Dumper(
                    new DropletsConfigurationsRepresenter(encryptor),
                    new DumperOptions()));

            final List<Object> allSettings = new LinkedList<Object>();
            for (final Configurable instance : configurableInstances) {
                allSettings.add(instance.getSettings());
            }

            yaml.dumpAll(allSettings.iterator(), fw);
        } catch (final IOException e) {
            LOGGER.error("Cannot write droplet settings", e);
        } finally {
            IOUtils.closeQuietly(fw);
        }
    }

    /**
     * Load all droplet settings from the default droplets configuration file.
     * 
     * @return settings if the file was loaded, {@code null} otherwise.
     */
    public Iterable<Object> loadDropletSettings() {
        assert (!SwingUtilities.isEventDispatchThread());

        final String homeDirectory = System.getProperty(USER_HOME);
        if (homeDirectory == null) {
            LOGGER.error("No home directory");
            return null;
        }
        File configFile = new File(homeDirectory, TIDAL_CONFIG_DIR);
        configFile = new File(configFile, DROPLETSRC);
        return loadDropletSettings(configFile);
    }

    /**
     * Load all droplet settings from the given file.
     * 
     * @param file
     * @return droplet settings if the file was loaded, {@code null} otherwise.
     */
    public Iterable<Object> loadDropletSettings(final File file) {
        assert (!SwingUtilities.isEventDispatchThread());

        FileReader fr = null;
        Iterable<Object> allSettings = null;
        try {
            fr = new FileReader(file);

            final Yaml yaml = new Yaml(new Loader(
                    new DropletsConfigurationsConstructor(encryptor)));
            allSettings = yaml.loadAll(fr);
        } catch (final FileNotFoundException e) {
            LOGGER.error("File not found", e);
        } finally {
            IOUtils.closeQuietly(fr);
        }
        return allSettings;
    }

    /**
     * Unlock the configuration in use.
     * 
     * @param authKey
     * @return true if authorized, false otherwise.
     */
    public boolean authorize(final String authKey) {
        assert (!SwingUtilities.isEventDispatchThread());

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
     */
    public void changeAuthorizationKey(final String newAuthKey)
            throws UnsecuredException {
        assert (!SwingUtilities.isEventDispatchThread());

        if (newAuthKey == null || newAuthKey.isEmpty()) {
            throw new UnsecuredException("Authorisation key cannot be blank.");
        }

        final StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
        config.setAuthKeyDigest(passwordEncryptor.encryptPassword(newAuthKey));

        encryptor.setPassword(newAuthKey);

        configurationUnlocked = true;
    }

    public void addConfigurable(final Configurable instance) {
        assert (!SwingUtilities.isEventDispatchThread());
        configurableInstances.add(instance);
    }

    public void removeConfigurable(final Configurable instance) {
        assert (!SwingUtilities.isEventDispatchThread());
        configurableInstances.remove(instance);
    }
}
