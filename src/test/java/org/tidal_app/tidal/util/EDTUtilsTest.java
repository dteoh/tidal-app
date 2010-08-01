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

import static org.junit.Assert.fail;

import javax.swing.SwingUtilities;

import org.junit.Test;
import org.tidal_app.tidal.exceptions.EDTViolationException;
import org.tidal_app.tidal.exceptions.LongOperationException;

/**
 * Unit tests for {@link EDTUtils}.
 * 
 * @author Douglas Teoh
 * 
 */
public class EDTUtilsTest {

    /**
     * Test if the class can be instantiated. Expecting nothing special.
     */
    @Test
    public void testEDTUtils() {
        new EDTUtils();
    }

    /**
     * No exception when called from the EDT.
     */
    @Test
    public void testInEDT1() {
        Runnable edtTask = new Runnable() {
            @Override
            public void run() {
                EDTUtils.inEDT();
            }
        };
        SwingUtilities.invokeLater(edtTask);
    }

    /**
     * Exception when called from outside the EDT.
     */
    @Test(expected = EDTViolationException.class)
    public void testInEDT2() {
        EDTUtils.inEDT();
    }

    /**
     * No exception when called from outside the EDT.
     */
    @Test
    public void testOutsideEDT1() {
        EDTUtils.outsideEDT();
    }

    /**
     * Exception when called from the EDT.
     */
    @Test
    public void testOutsideEDT2() {
        Runnable nonEdtTask = new Runnable() {
            @Override
            public void run() {
                try {
                    EDTUtils.outsideEDT();
                    fail("Expecting LongOperationException.");
                } catch (LongOperationException e) {
                    // Test passed.
                }
            }
        };
        SwingUtilities.invokeLater(nonEdtTask);
    }

    /**
     * Test that the helper function runs the task in the EDT if calling from
     * outside the EDT.
     */
    @Test
    public void testRunOnEDT1() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                if (!SwingUtilities.isEventDispatchThread()) {
                    fail("Expecting to be in EDT.");
                }
            }
        };
        EDTUtils.runOnEDT(task);
    }

    /**
     * Test that the helper function runs the task in the EDT if calling from
     * the EDT.
     */
    @Test
    public void testRunOnEDT2() {
        final Runnable task = new Runnable() {
            @Override
            public void run() {
                if (!SwingUtilities.isEventDispatchThread()) {
                    fail("Expecting to be in EDT.");
                }
            }
        };
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                EDTUtils.runOnEDT(task);
            }
        });
    }

}
