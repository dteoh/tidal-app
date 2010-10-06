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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import javax.swing.Icon;

/**
 * Used to create an animated icon. AnimatedIcon is not thread-safe.
 * 
 * @author Douglas Teoh
 * 
 */
public class AnimatedIcon implements Icon {

    /** Frame images. */
    private final Image frames;

    /** Frame width. */
    private final int frameWidth;

    /** Frame height. */
    private final int frameHeight;

    /** Current icon frame index. */
    private int frameIdx;

    /** Total number of frames. */
    private final int frameLen;

    public AnimatedIcon(final Image frames, final int frameWidth,
            final int frameHeight) {
        if (frames == null) {
            throw new NullPointerException("Null frames");
        }
        if (frameWidth <= 0 || frameHeight <= 0) {
            throw new IllegalArgumentException("Invalid frame dimension");
        }

        this.frames = frames;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;

        frameIdx = 0;
        frameLen = computeFrameLength();
    }

    /**
     * Advance the animation by one frame.
     */
    public void nextFrame() {
        frameIdx = ++frameIdx % frameLen;
    }

    @Override
    public void paintIcon(final Component c, final Graphics g, final int x,
            final int y) {
        Point offset = computeFrameOffset(frameIdx);
        g.drawImage(frames, x, y, x + frameWidth, y + frameHeight, offset.x,
                offset.y, offset.x + frameWidth, offset.y + frameHeight, null);
    }

    @Override
    public int getIconWidth() {
        return frameWidth;
    }

    @Override
    public int getIconHeight() {
        return frameHeight;
    }

    private Point computeFrameOffset(final int i) {
        int horFrames = frames.getWidth(null) / frameWidth;

        int vOffset = 0;
        int hOffset = 0;

        // Compute vertical offset.
        int idx = i;
        while (idx > horFrames) {
            vOffset += frameHeight;
            idx -= horFrames;
        }

        // Compute horizontal offset.
        hOffset = idx * frameWidth;

        Point offset = new Point(hOffset, vOffset);
        return offset;
    }

    private int computeFrameLength() {
        int horFrames = frames.getWidth(null) / frameWidth;
        int verFrames = frames.getHeight(null) / frameHeight;
        return horFrames * verFrames;
    }

}
