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

package org.tidal_app.tidal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tidal_app.tidal.configuration.ConfigurationController;
import org.tidal_app.tidal.sources.email.EmailDropletsController;

/**
 * Main application entry point.
 * 
 * @author Douglas Teoh
 */
public class Tidal {

    private static final Logger LOGGER = LoggerFactory.getLogger(Tidal.class);

    /**
     * Entry point into the program.
     * 
     * @param args
     */
    public static void main(final String[] args) {
        // Set the application to use system UI LnF.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            LOGGER.error("Look and feel error", e);
        } catch (InstantiationException e) {
            LOGGER.error("Look and feel error", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("Look and feel error", e);
        } catch (UnsupportedLookAndFeelException e) {
            LOGGER.error("Look and feel error", e);
        }

        /*
         * These controllers are being instantiated here because they contain no
         * UI elements; as such, letting TidalController instantiate them is
         * probably a bad idea a the classes will be instantiated in the EDT,
         * which is not what we want.
         */
        final ConfigurationController configurationController =
            new ConfigurationController();
        final EmailDropletsController emailDropletsController =
            new EmailDropletsController();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TidalController(configurationController,
                        emailDropletsController);
            }
        });
    }

}
