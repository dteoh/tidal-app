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

package org.tidal_app.tidal.sources.email.models;

/**
 * Stores the settings for an email droplet.
 * 
 * @author Douglas Teoh
 */
public final class EmailSettings {

    private final String host;
    private final Protocol protocol;
    private final String username;
    private final String password;

    /**
     * 
     * @param host
     *            host address
     * @param protocol
     *            email protocol
     * @param username
     *            username
     * @param password
     *            password
     */
    public EmailSettings(final String host, final Protocol protocol,
            final String username, final String password) {
        if (protocol == null) {
            throw new NullPointerException();
        }

        this.host = host;
        this.protocol = protocol;
        this.username = username;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result
                + ((password == null) ? 0 : password.hashCode());
        result = prime * result
                + ((protocol == null) ? 0 : protocol.hashCode());
        result = prime * result
                + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        EmailSettings other = (EmailSettings) obj;
        if (host == null) {
            if (other.host != null) {
                return false;
            }
        } else if (!host.equals(other.host)) {
            return false;
        }
        if (password == null) {
            if (other.password != null) {
                return false;
            }
        } else if (!password.equals(other.password)) {
            return false;
        }
        if (protocol != other.protocol) {
            return false;
        }
        if (username == null) {
            if (other.username != null) {
                return false;
            }
        } else if (!username.equals(other.username)) {
            return false;
        }
        return true;
    }

}
