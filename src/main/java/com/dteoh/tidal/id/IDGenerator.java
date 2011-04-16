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

package com.dteoh.tidal.id;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Used to generate ID objects.
 * 
 * @author Douglas Teoh
 * 
 */
public enum IDGenerator {

    /** The only instance of an ID generator. */
    INSTANCE;

    private volatile AtomicLong seqNum = new AtomicLong();

    /**
     * Generates a unique identifer.
     * 
     * @return generated identifier
     */
    public static synchronized ID generateID() {
        return new NumericID(INSTANCE.seqNum.getAndIncrement());
    }

    /** Private implementation of ID objects. */
    private static final class NumericID implements ID {
        private final long number;

        /**
         * Constructs a new numerical identifier.
         * 
         * @param number
         */
        public NumericID(final long number) {
            this.number = number;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (number ^ (number >>> 32));
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
            NumericID other = (NumericID) obj;
            if (number != other.number) {
                return false;
            }
            return true;
        }
    }
}
