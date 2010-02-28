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

/**
 * Generic email droplet.
 * 
 * @author douglas
 */
public abstract class EmailDroplet implements Droplet {

    protected final String username;
    protected final String password;
    protected final String host;
    protected final String protocol;

    public EmailDroplet(final String host, final String protocol,
            final String username, final String password) {
        this.host = host;
        this.protocol = protocol;
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public abstract void init() throws DropletInitException;

    public abstract void destroy();

    /**
     * @return Most recent EmailRipple objects.
     */
    public abstract Iterable<EmailRipple> getRipples();

}
