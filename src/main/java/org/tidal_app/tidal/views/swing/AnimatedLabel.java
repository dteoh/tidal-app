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

package org.tidal_app.tidal.views.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.Timer;

/**
 * A JLabel implementation for displaying an {@link AnimatedIcon}. Handles the
 * timing functionality required for animation.
 * 
 * @author Douglas Teoh
 * 
 */
public class AnimatedLabel extends JLabel {

    private final AnimatedIcon aniIcon;
    private final int frameInterval;
    private final Timer frameTimer;

    /**
     * Creates a new AnimatedLabel.
     * 
     * @param icon
     *            AnimatedIcon to display on this label.
     * @param frameInterval
     *            Interval between frames measured in milliseconds.
     */
    public AnimatedLabel(final AnimatedIcon icon, final int frameInterval) {
        super(icon);
        if (icon == null) {
            throw new NullPointerException();
        }
        if (frameInterval <= 0) {
            throw new IllegalArgumentException();
        }

        aniIcon = icon;
        this.frameInterval = frameInterval;

        frameTimer = new Timer(frameInterval, new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                aniIcon.nextFrame();
                repaint();
            }
        });
    }

    /**
     * Start animating the label.
     */
    public void start() {
        frameTimer.start();
    }

    /**
     * Stop animating the label.
     */
    public void stop() {
        frameTimer.stop();
    }

}
