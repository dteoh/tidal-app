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

package org.tidal_app.tidal.sources.email;

import org.tidal_app.tidal.exceptions.DropletInitException;
import org.tidal_app.tidal.sources.Droplet;
import org.tidal_app.tidal.sources.email.models.EmailRipple;
import org.tidal_app.tidal.sources.email.models.EmailSettings;

/**
 * Generic email droplet.
 * 
 * @author Douglas Teoh
 */
public abstract class AbstractEmailDroplet implements Droplet {

    protected final EmailSettings settings;

    protected AbstractEmailDroplet(final EmailSettings settings) {
        this.settings = settings;
    }

    protected AbstractEmailDroplet(final String host, final String protocol,
            final String username, final String password) {
        settings = new EmailSettings();
        settings.setHost(host);
        settings.setPassword(password);
        settings.setProtocol(protocol);
        settings.setUsername(username);
    }

    public String getUsername() {
        return settings.getUsername();
    }

    public Object getSettings() {
        return settings.makeCopy();
    }

    public abstract void init() throws DropletInitException;

    public abstract void destroy();

    /**
     * @return Most recent EmailRipple objects.
     */
    public abstract Iterable<EmailRipple> getRipples();

}
