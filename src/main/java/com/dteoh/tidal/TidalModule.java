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

package com.dteoh.tidal;


import com.dteoh.tidal.configuration.ConfigurationController;
import com.dteoh.tidal.configuration.SaveConfigurable;
import com.dteoh.tidal.controllers.DropletsViewManager;
import com.dteoh.tidal.controllers.MenuBarController;
import com.dteoh.tidal.controllers.TidalController;
import com.dteoh.tidal.controllers.ViewManager;
import com.dteoh.tidal.guice.LoggerListener;
import com.dteoh.tidal.sources.email.EmailDropletsController;
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
