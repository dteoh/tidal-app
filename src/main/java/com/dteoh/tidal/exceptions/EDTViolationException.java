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

import org.jdesktop.application.ResourceMap;

import com.dteoh.treasuremap.ResourceMaps;

/**
 * Exception class for EDT thread violations.
 * 
 * @author Douglas Teoh
 * 
 */
public final class EDTViolationException extends IllegalStateException {

    private static final ResourceMap BUNDLE = new ResourceMaps(
            EDTViolationException.class).build();

    public EDTViolationException() {
        super(BUNDLE.getString("EDTViolationException.message"));
    }

}
