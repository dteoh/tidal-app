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

import org.tidal_app.tidal.configuration.models.Configurable;

/**
 * Interface for adding and removing a {@link Configurable} droplet for droplet
 * settings persistence.
 * 
 * @author Douglas Teoh
 * 
 */
public interface SaveConfigurable {

    /**
     * Add a Configurable object for tracking. The object is assumed to be a
     * droplet, as such, the object will be serialized to the droplet settings
     * file.
     * 
     * @param droplet
     *            The object to track.
     * @return true if the object is being tracked. false if the object is null
     *         or if the object is already being tracked (some other object is
     *         equal to the given object).
     */
    boolean addConfigurable(Configurable droplet);

    /**
     * Remove a Configurable object.
     * 
     * @param droplet
     *            The object to remove.
     * @return true if the object was removed. false if the given object is null
     *         or if it is not being tracked.
     */
    boolean removeConfigurable(Configurable droplet);

}
