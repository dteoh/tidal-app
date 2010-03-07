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

    private final static Logger LOGGER =
        LoggerFactory.getLogger(ConfigurationController.class);

    /** Contains master configuration information */
    private Configuration config;
    /** Symmetric key cryptography */
    private final StrongTextEncryptor encryptor;

    private final Set<Configurable> configurableInstances;

    public ConfigurationController() {
        config = new Configuration();
        encryptor = new StrongTextEncryptor();
        configurableInstances = new HashSet<Configurable>();
    }

    /**
     * Load the given config file. If the file doesn't exist, the current loaded
     * config is unchanged.
     * 
     * @param filePath
     */
    public void loadMainSettings(final String filePath) {
        assert (!SwingUtilities.isEventDispatchThread());

        File file = new File(filePath);
        loadMainSettings(file);
    }

    /**
     * Load the given config file. If the file doesn't exist, the current loaded
     * config is unchanged.
     * 
     * @param file
     */
    public void loadMainSettings(final File file) {
        assert (!SwingUtilities.isEventDispatchThread());

        FileReader fr = null;
        try {
            fr = new FileReader(file);
            Yaml yaml = new Yaml();
            config = (Configuration) yaml.load(fr);
        } catch (FileNotFoundException e) {
            LOGGER.error("File not found", e);
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    LOGGER.error("Could not close reader", e);
                }
            }
        }
    }

    /**
     * Save the configuration file to the given file path.
     * 
     * @param filePath
     * @throws UnsecuredException
     *             if no authorization key is set.
     */
    public void saveMainSettings(final String filePath)
            throws UnsecuredException {
        assert (!SwingUtilities.isEventDispatchThread());

        File file = new File(filePath);
        saveMainSettings(file);
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

        if (config.getAuthKeyDigest().isEmpty()
            || config.getAuthKeyDigest() == null) {
            throw new UnsecuredException("Authorisation key cannot be blank.");
        }

        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            Yaml yaml = new Yaml();
            yaml.dump(config, fw);
        } catch (IOException e) {
            LOGGER.error("Cannot write config", e);
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    LOGGER.error("Could not close writer", e);
                }
            }
        }
    }

    /**
     * Saves all current Droplet settings to the given file.
     * 
     * @param filePath
     * @throws UnsecuredException
     *             if there is no authorization key
     */
    public void saveDropletSettings(final String filePath)
            throws UnsecuredException {
        assert (!SwingUtilities.isEventDispatchThread());

        File file = new File(filePath);
        saveDropletSettings(file);
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

        if (config.getAuthKeyDigest().isEmpty()
            || config.getAuthKeyDigest() == null) {
            throw new UnsecuredException("Authorisation key cannot be blank.");
        }

        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            Yaml yaml =
                new Yaml(new Dumper(new DropletsConfigurationsRepresenter(
                        encryptor), new DumperOptions()));

            List<Object> allSettings = new LinkedList<Object>();
            for (Configurable instance : configurableInstances) {
                allSettings.add(instance.getSettings());
            }

            yaml.dumpAll(allSettings.iterator(), fw);
        } catch (IOException e) {
            LOGGER.error("Cannot write droplet settings", e);
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    LOGGER.error("Could not close writer", e);
                }
            }
        }
    }

    /**
     * Load all droplet settings from the given file.
     * 
     * @param filePath
     * @return droplet settings if the file was loaded, {@code null} otherwise.
     */
    public Iterable<Object> loadDropletSettings(final String filePath) {
        assert (!SwingUtilities.isEventDispatchThread());

        File file = new File(filePath);
        return loadDropletSettings(file);
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

            Yaml yaml =
                new Yaml(new Loader(new DropletsConfigurationsConstructor(
                        encryptor)));
            allSettings = yaml.loadAll(fr);
        } catch (FileNotFoundException e) {
            LOGGER.error("File not found", e);
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    LOGGER.error("Could not close reader", e);
                }
            }
        }
        return allSettings;
    }

    /**
     * Unlock the configuration in use.
     * 
     * @param authKey
     * @return true if authorized, false otherwise.
     */
    public boolean authorise(final String authKey) {
        assert (!SwingUtilities.isEventDispatchThread());

        StrongPasswordEncryptor passwordEncryptor =
            new StrongPasswordEncryptor();
        if (passwordEncryptor.checkPassword(authKey, config.getAuthKeyDigest())) {
            encryptor.setPassword(authKey);
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
    public void changeAuthorisationKey(final String newAuthKey)
            throws UnsecuredException {
        assert (!SwingUtilities.isEventDispatchThread());

        if (newAuthKey == null || newAuthKey.isEmpty()) {
            throw new UnsecuredException("Authorisation key cannot be blank.");
        }

        StrongPasswordEncryptor passwordEncryptor =
            new StrongPasswordEncryptor();
        config.setAuthKeyDigest(passwordEncryptor.encryptPassword(newAuthKey));

        // TODO decrypt other stored passwords and re-encrypt with new key.

        encryptor.setPassword(newAuthKey);
    }

    public synchronized void addConfigurable(final Configurable instance) {
        assert (!SwingUtilities.isEventDispatchThread());

        configurableInstances.add(instance);
    }

    public synchronized void removeConfigurable(final Configurable instance) {
        assert (!SwingUtilities.isEventDispatchThread());

        configurableInstances.remove(instance);
    }
}
