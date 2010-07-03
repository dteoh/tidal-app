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

package org.tidal_app.tidal;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.junit.Test;

/**
 * Unit tests for {@link MenuBarController}.
 * 
 * @author Douglas Teoh
 * 
 */
public class MenuBarControllerTests {

    /**
     * Test creation out of EDT. TODO: remove once functionality has been
     * implemented.
     */
    @Test
    public void testMenuBarController1() {
        MenuBarController mb = new MenuBarController();
        mb.getView();
    }

    /**
     * Test creation on EDT. TODO: remove once functionality has been
     * implemented.
     * 
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    @Test
    public void testMenuBarController2() throws InterruptedException,
            InvocationTargetException {
        Runnable edtTask = new Runnable() {
            @Override
            public void run() {
                MenuBarController mb = new MenuBarController();
                mb.getView();
            }
        };
        SwingUtilities.invokeAndWait(edtTask);
    }

}
