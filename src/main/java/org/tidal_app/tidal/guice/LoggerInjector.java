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

package org.tidal_app.tidal.guice;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.MembersInjector;

/**
 * Injects logger fields with an instantiated logger.
 * 
 * @author Douglas Teoh
 * 
 */
public class LoggerInjector<T> implements MembersInjector<T> {

    private final Field field;
    private final Logger logger;

    public LoggerInjector(final Field field) {
        this.field = field;
        logger = LoggerFactory.getLogger(field.getDeclaringClass());
        field.setAccessible(true);
    }

    @Override
    public void injectMembers(final T arg) {
        try {
            field.set(arg, logger);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
