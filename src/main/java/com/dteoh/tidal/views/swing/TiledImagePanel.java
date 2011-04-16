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

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

/**
 * Used to create a panel with a tiled image background.
 * 
 * @author Douglas Teoh
 */
public class TiledImagePanel extends JPanel {

    private Image background;

    public void setBackground(final Image background) {
        this.background = background;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        if (background == null) {
            super.paintComponent(g);
            return;
        }

        final int imageWidth = background.getWidth(null);
        final int imageHeight = background.getHeight(null);

        final int panelWidth = getWidth();
        final int panelHeight = getHeight();
        for (int xCoord = 0; xCoord < panelWidth; xCoord += imageWidth) {
            for (int yCoord = 0; yCoord < panelHeight; yCoord += imageHeight) {
                g.drawImage(background, xCoord, yCoord, null);
            }
        }
    }
}
