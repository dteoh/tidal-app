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

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.jasypt.util.text.StrongTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tidal_app.tidal.exceptions.UnsecuredException;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

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

    public ConfigurationController() {
        config = new Configuration();
        encryptor = new StrongTextEncryptor();
    }

    /**
     * Load the given config file. If the file doesn't exist, the current loaded
     * config is unchanged.
     * 
     * @param filePath
     */
    public void load(final String filePath) {
        File file = new File(filePath);
        load(file);
    }

    /**
     * Load the given config file. If the file doesn't exist, the current loaded
     * config is unchanged.
     * 
     * @param file
     */
    public void load(final File file) {
        if (!file.exists()) {
            return;
        }
        FileReader fr = null;
        try {
            fr = new FileReader(file);
            Gson gson = new Gson();
            config = gson.fromJson(fr, Configuration.class);
        } catch (FileNotFoundException e) {
            LOGGER.error("File not found", e);
        } catch (JsonParseException e) {
            LOGGER.error("Failed to parse config", e);
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
    public void save(final String filePath) throws UnsecuredException {
        File file = new File(filePath);
        save(file);
    }

    /**
     * Save the configuration file to the given file path.
     * 
     * @param file
     * @throws UnsecuredException
     *             if no authorization key is set.
     */
    public void save(final File file) throws UnsecuredException {
        if (config.getAuthKeyDigest().isEmpty()
            || config.getAuthKeyDigest() == null) {
            throw new UnsecuredException("Authorisation key cannot be blank.");
        }

        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            Gson gson = new Gson();
            gson.toJson(config, fw);
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
     * Unlock the configuration in use.
     * 
     * @param authKey
     * @return true if authorized, false otherwise.
     */
    public boolean authorise(final String authKey) {
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
     */
    public void changeAuthorisationKey(final String newAuthKey) {
        StrongPasswordEncryptor passwordEncryptor =
            new StrongPasswordEncryptor();
        config.setAuthKeyDigest(passwordEncryptor.encryptPassword(newAuthKey));
    }
}
