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

package org.tidal_app.tidal.util;

import javax.swing.SwingUtilities;

import org.tidal_app.tidal.exceptions.EDTViolationException;
import org.tidal_app.tidal.exceptions.LongOperationException;

/**
 * Utility class for EDT-related checks.
 * 
 * @author Douglas Teoh
 * 
 */
public final class EDTUtils {

    /**
     * Used to throw a new {@link EDTViolationException} if caller is not
     * currently in the EDT.
     */
    public static void inEDT() {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new EDTViolationException();
        }
    }

    /**
     * Used to throw a new {@link LongOperationException} if caller should not
     * be invoked on the EDT.
     */
    public static void outsideEDT() {
        if (SwingUtilities.isEventDispatchThread()) {
            throw new LongOperationException();
        }
    }

}