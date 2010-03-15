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

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.tidal_app.tidal.views.MenuBarView;

/**
 * This class is responsible for managing the menu bar view and events generated
 * from it.
 * 
 * @author Douglas Teoh
 */
public class MenuBarController {

    private transient MenuBarView menuBar;

    public MenuBarController() {
        initView();
    }

    private void initView() {
        final Runnable swingTask = new Runnable() { // NOPMD by Douglas Teoh on
                                                    // 15/03/10 8:01 PM
                @Override
                public void run() {
                    menuBar = new MenuBarView();
                }
            };
        if (SwingUtilities.isEventDispatchThread()) {
            swingTask.run();
        } else {
            SwingUtilities.invokeLater(swingTask);
        }
    }

    public JComponent getView() {
        return menuBar;
    }

}
