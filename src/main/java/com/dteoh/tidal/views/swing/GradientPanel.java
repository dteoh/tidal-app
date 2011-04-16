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

package com.dteoh.tidal.views.swing;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.JPanel;

/**
 * Used to create a panel with a two-point, top-to-bottom, linear gradient
 * background.
 * 
 * @author Douglas Teoh
 */
public class GradientPanel extends JPanel {

    private final Color topGradient;
    private final Color bottomGradient;

    /**
     * Creates a gradient panel with the two specified colors.
     * 
     * @param top
     *            Color to start from the top
     * @param bottom
     *            Color to end at the bottom
     */
    public GradientPanel(final Color top, final Color bottom) {
        super();
        topGradient = top;
        bottomGradient = bottom;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        final Graphics2D g2 = (Graphics2D) g;
        final GradientPaint gradient = new GradientPaint(0, 0, topGradient, 0,
                getHeight(), bottomGradient);

        // Store old state.
        final Paint oldPaint = g2.getPaint();

        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Restore old state.
        g2.setPaint(oldPaint);
    }

}
