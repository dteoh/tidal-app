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

package com.dteoh.tidal.controllers;

import static com.dteoh.tidal.util.EDTUtils.inEDT;

import javax.swing.JComponent;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;


import com.dteoh.tidal.util.EDTUtils;
import com.dteoh.tidal.views.View;

/**
 * Displays Views on the user interface.
 * 
 * @author Douglas Teoh
 */
public class DropletsViewManager implements ViewManager, View {

    /** View objects */
    private JPanel dropletsPanel;

    /**
     * 
     * @return
     */
    public static DropletsViewManager create() {
        return new DropletsViewManager();
    }

    private DropletsViewManager() {
        initView();
    }

    /** Initialize the view. */
    private void initView() {
        final Runnable swingTask = new Runnable() {
            @Override
            public void run() {
                dropletsPanel = new JPanel(new MigLayout("wrap", "[grow 100]"));
                dropletsPanel.setOpaque(false);
            }
        };

        EDTUtils.runOnEDT(swingTask);
    }

    @Override
    public JComponent getView() {
        inEDT();
        return dropletsPanel;
    }

    @Override
    public void displayView(final View view) {
        inEDT();
        if (view == null || view.getView() == null) {
            throw new NullPointerException();
        }
        dropletsPanel.add(view.getView(), "growx,pushx");
        dropletsPanel.validate();
    }

    @Override
    public void removeView(final View view) {
        inEDT();
        if (view == null || view.getView() == null) {
            throw new NullPointerException();
        }
        dropletsPanel.remove(view.getView());
        dropletsPanel.validate();
        dropletsPanel.repaint();
    }
}
