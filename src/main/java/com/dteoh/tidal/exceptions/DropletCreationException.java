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

package com.dteoh.tidal.exceptions;

/**
 * This exception is thrown when a Droplet cannot be created.
 * 
 * @author Douglas Teoh
 */
public class DropletCreationException extends Exception {

    public DropletCreationException() {
        super();
    }

    public DropletCreationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DropletCreationException(final String message) {
        super(message);
    }

    public DropletCreationException(final Throwable cause) {
        super(cause);
    }

}
