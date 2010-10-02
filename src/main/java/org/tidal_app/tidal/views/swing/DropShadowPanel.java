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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import javax.swing.JPanel;

/**
 * This panel paints a gaussian drop shadow underneath the panel. The drop
 * shadow is painted as if the light source is shining directly onto the center
 * of the panel.
 * 
 * @author Douglas Teoh
 */
public class DropShadowPanel extends JPanel {

    /** Shadow radius. */
    private int size;
    /** Shadow opacity. */
    private float opacity;
    /** Cached drop shadow. */
    private BufferedImage dropShadow;

    /**
     * Creates a new drop shadow panel.
     * 
     * @param size
     *            Size of the shadow
     * @param opacity
     *            Opacity of the shadow, from 0.0 (transparent) to 1.0 (opaque)
     */
    public DropShadowPanel(final int size, final float opacity) {
        super();

        if (size < 0) {
            this.size = 0;
        } else {
            this.size = size;
        }

        if (opacity < 0.0F) {
            this.opacity = 0;
        } else if (opacity > 1.0F) {
            this.opacity = 1;
        } else {
            this.opacity = opacity;
        }

        // Invalidate the cache when the panel resizes.
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                dropShadow = null;
                repaint();
            }
        });

    }

    @Override
    protected void paintComponent(final Graphics g) {
        final Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.SrcOver.derive(opacity));

        if (dropShadow != null) {
            // Paint from cache.
            g2.drawImage(dropShadow, -size, -size, null);
        } else {

            BufferedImage shadow = new BufferedImage(getWidth() - 2 * size,
                    getHeight() - 2 * size, BufferedImage.TYPE_INT_ARGB);

            for (int x = 0; x < shadow.getWidth(); x++) {
                for (int y = 0; y < shadow.getHeight(); y++) {
                    shadow.setRGB(x, y, 0xFF000000);
                }
            }
            shadow = createDropShadow(shadow, size);
            g2.drawImage(shadow, -size, -size, null);

            dropShadow = shadow;
        }
        g2.dispose();
    }

    /**
     * Creates a drop shadow for the given image. This function is from the book
     * "Filthy Rich Clients" in Chapter 16, "Realistic Drop Shadow" section.
     * 
     * @param image
     *            source image to geneate the shadow for.
     * @param size
     *            Size of the drop shadow.
     * @return image of the shadow.
     */
    private BufferedImage createDropShadow(final BufferedImage image,
            final int size) {
        BufferedImage shadow = new BufferedImage(image.getWidth() + 4 * size,
                image.getHeight() + 4 * size, BufferedImage.TYPE_INT_ARGB);

        final Graphics2D g2 = shadow.createGraphics();
        g2.drawImage(image, size * 2, size * 2, null);

        g2.setComposite(AlphaComposite.SrcIn);
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, shadow.getWidth(), shadow.getHeight());

        g2.dispose();

        shadow = getGaussianBlurFilter(size, true).filter(shadow, null);
        shadow = getGaussianBlurFilter(size, false).filter(shadow, null);

        return shadow;
    }

    /**
     * Creates a convolution kernel representing a gaussian blur. The kernel is
     * a vector. This function is from the book "Filthy Rich Clients" in Chapter
     * 16, "Gaussian Blur" section.
     * 
     * @param radius
     *            radius of the blur.
     * @param horizontal
     *            generate a row or column vector.
     * @return Convolution kernel representing the gaussian blur.
     */
    private ConvolveOp getGaussianBlurFilter(final int radius,
            final boolean horizontal) {
        if (radius < 1) {
            throw new IllegalArgumentException("Radius muse be >= 1");
        }

        final int size = radius * 2 + 1;
        final float[] data = new float[size];

        final float sigma = radius / 3.0F;
        final float twoSigmaSquare = 2.0F * sigma * sigma;
        final float sigmaRoot = (float) Math.sqrt(twoSigmaSquare * Math.PI);
        float total = 0.0F;

        for (int i = -radius; i <= radius; i++) {
            final float distance = i * i;
            final int index = i + radius;
            data[index] = (float) (Math.exp(-distance / twoSigmaSquare) / sigmaRoot);
            total += data[index];
        }

        for (int i = 0; i < data.length; i++) {
            data[i] /= total;
        }

        Kernel kernel = null;
        if (horizontal) {
            kernel = new Kernel(size, 1, data);
        } else {
            kernel = new Kernel(1, size, data);
        }
        return new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
    }
}
