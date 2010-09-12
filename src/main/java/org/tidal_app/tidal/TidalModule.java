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

import org.tidal_app.tidal.configuration.ConfigurationController;
import org.tidal_app.tidal.configuration.SaveConfigurable;
import org.tidal_app.tidal.controllers.DropletsViewManager;
import org.tidal_app.tidal.controllers.MenuBarController;
import org.tidal_app.tidal.controllers.TidalController;
import org.tidal_app.tidal.controllers.ViewManager;
import org.tidal_app.tidal.guice.LoggerListener;
import org.tidal_app.tidal.sources.email.EmailDropletsController;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

/**
 * Configures the Tidal class bindings.
 * 
 * @author Douglas Teoh
 * 
 */
public class TidalModule extends AbstractModule {

    @Override
    protected void configure() {

        DropletsViewManager dropletsViewC = DropletsViewManager.create();
        bind(DropletsViewManager.class).toInstance(dropletsViewC);
        bind(ViewManager.class).toInstance(dropletsViewC);

        ConfigurationController configC = new ConfigurationController();
        bind(ConfigurationController.class).toInstance(configC);
        bind(SaveConfigurable.class).toInstance(configC);

        bind(MenuBarController.class);

        bind(EmailDropletsController.class);

        bind(TidalController.class);

        bindListener(Matchers.any(), new LoggerListener());
    }

}
