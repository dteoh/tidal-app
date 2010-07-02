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

package org.tidal_app.tidal.exceptions;

/**
 * Exception class for operations that should not be invoked on the EDT.
 * 
 * @author Douglas Teoh
 * 
 */
public class LongOperationException extends IllegalStateException {

    /** Default exception message. */
    private static final String MESSAGE = "An expensive operation was invoked on the EDT; please refactor.";

    public LongOperationException() {
        super(MESSAGE);
    }

}
