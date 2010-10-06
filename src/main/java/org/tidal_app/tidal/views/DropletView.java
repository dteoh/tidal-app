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

package org.tidal_app.tidal.views;

import org.tidal_app.tidal.views.events.DropletViewListener;
import org.tidal_app.tidal.views.models.DropletModel;

/**
 * Interface for modifying a droplet view.
 * 
 * @author Douglas Teoh
 * 
 */
public interface DropletView extends View {

    /**
     * Sets the {@link DropletModel} to visualize.
     * 
     * @param model
     */
    void setDropletModel(final DropletModel model);

    /**
     * Add a {@link DropletModel} to the existing visualization.
     * 
     * @param model
     */
    void addDropletModel(final DropletModel model);

    /**
     * Adds the specified droplet view listener to receive droplet view events
     * from this view.
     * 
     * @param listener
     */
    void addDropletViewListener(final DropletViewListener listener);

    /**
     * Removes the specified droplet view listener so that it no longer receives
     * droplet view events from this view.
     * 
     * @param listener
     */
    void removeDropletViewListener(final DropletViewListener listener);

    /**
     * Sets the droplet updating status.
     * 
     * @param status
     *            True if the droplet is currently updating, false otherwise.
     */
    void dropletUpdating(boolean status);

}
