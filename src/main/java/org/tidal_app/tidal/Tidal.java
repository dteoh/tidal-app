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

import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tidal_app.tidal.controllers.TidalController;

import com.google.inject.Guice;
import com.google.inject.Injector;

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
        } catch (final Exception e) {
            LOGGER.error("Look and feel error", e);
        }

        // ConfigurationController configurationC = new
        // ConfigurationController();
        // EmailDropletsController emailDropletsC = new
        // EmailDropletsController();
        // new TidalController(configurationC, emailDropletsC);

        Injector injector = Guice.createInjector(new TidalModule());
        injector.getInstance(TidalController.class);
    }

}
