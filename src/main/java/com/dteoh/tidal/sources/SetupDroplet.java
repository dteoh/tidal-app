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

package com.dteoh.tidal.sources;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * Interface for retrieving and interacting with droplet setup through the UI.
 * 
 * @author Douglas Teoh
 * 
 */
public interface SetupDroplet {

    /**
     * Retrieves the account setup view for creating a new droplet.
     * 
     * @return Account setup view.
     */
    JComponent getSetupView();

    /**
     * Retrieves the account setup icon for creating a new droplet.
     * 
     * @return Account setup icon.
     */
    Icon getSetupIcon();

    /**
     * Cancel the setup process.
     */
    void cancelSetup();

    /**
     * Create a new droplet from the information in the setup view.
     * 
     * @return True if a new droplet was created.
     */
    boolean createDropletFromSetup();

}
