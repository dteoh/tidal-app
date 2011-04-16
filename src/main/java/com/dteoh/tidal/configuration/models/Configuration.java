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

package com.dteoh.tidal.configuration.models;

/**
 * Master configuration model, might eventually expand to include program
 * settings.
 * 
 * @author Douglas Teoh
 */
public final class Configuration {

    private final String authKeyDigest;

    public Configuration(final String authKeyDigest) {
        this.authKeyDigest = authKeyDigest;
    }

    public String getAuthKeyDigest() {
        return authKeyDigest;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((authKeyDigest == null) ? 0 : authKeyDigest.hashCode());
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
        final Configuration other = (Configuration) obj;
        if (authKeyDigest == null) {
            if (other.authKeyDigest != null) {
                return false;
            }
        } else if (!authKeyDigest.equals(other.authKeyDigest)) {
            return false;
        }
        return true;
    }

}
