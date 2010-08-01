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

package org.tidal_app.tidal.controllers;

import static org.mockito.Mockito.mock;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tidal_app.tidal.views.events.MenuBarViewListener;

/**
 * Unit tests for {@link MenuBarController}.
 * 
 * @author Douglas Teoh
 * 
 */
public class MenuBarControllerTests {

    private MenuBarController mbc;

    @Before
    public void setUp() {
        mbc = new MenuBarController();
    }

    @After
    public void tearDown() {
        mbc = null;
    }

    /**
     * Test creation out of EDT.
     */
    @Test
    public void testMenuBarController1() {
        MenuBarController mb = new MenuBarController();
        mb.getView();
    }

    /**
     * Test creation on EDT.
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

    @Test
    public void testAddMenuBarViewListener1() {
        MenuBarViewListener listener = mock(MenuBarViewListener.class);
        mbc.addMenuBarViewListener(listener);
    }

    @Test
    public void testRemoveMenuBarViewListener1() {
        MenuBarViewListener listener = mock(MenuBarViewListener.class);
        mbc.addMenuBarViewListener(listener);
        mbc.removeMenuBarViewListener(listener);
    }

}
