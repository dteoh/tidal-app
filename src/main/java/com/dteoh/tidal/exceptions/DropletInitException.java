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

import com.dteoh.tidal.sources.Droplet;

/**
 * This exception is thrown when the call to {@link Droplet#init()} results in
 * failure.
 * 
 * @author Douglas Teoh
 */
public class DropletInitException extends Exception {

    public DropletInitException() {
        super();
    }

    public DropletInitException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DropletInitException(final String message) {
        super(message);
    }

    public DropletInitException(final Throwable cause) {
        super(cause);
    }

}
