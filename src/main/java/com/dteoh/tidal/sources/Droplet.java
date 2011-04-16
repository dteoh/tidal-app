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


import com.dteoh.tidal.configuration.models.Configurable;
import com.dteoh.tidal.exceptions.DropletInitException;
import com.dteoh.tidal.views.DropletView;

/**
 * A Droplet represents an arbitrary source of information.
 * 
 * @author Douglas Teoh
 */
public interface Droplet extends Configurable {

    /**
     * Initialize the droplet.
     * 
     * @throws DropletInitException
     */
    void init() throws DropletInitException;

    /**
     * Ask the droplet to retrieve new information and display it on the view
     * defined by {@link #getDropletView()}.
     */
    void update();

    /**
     * Destroy the droplet.
     */
    void destroy();

    /**
     * Retrieves the view associated with this droplet.
     */
    DropletView getDropletView();

}
