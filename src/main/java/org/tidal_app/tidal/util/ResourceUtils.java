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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Utility methods for dealing with class resources.
 * 
 * @author Douglas Teoh
 * 
 */
public final class ResourceUtils {

    /**
     * Retrieve an image resource from the given class's classloader.
     * 
     * @param c
     *            Class to use.
     * @param resourceName
     *            Name of the resource.
     * @return The image resource.
     * @throws IOException
     *             If an error occurs during image loading.
     */
    public static BufferedImage getImage(final Class<?> c,
            final String resourceName) throws IOException {
        return ImageIO.read(c.getResource(resourceName));
    }

    /**
     * Parses a string representing a dimension into a {@link Dimension}.
     * 
     * @param dimensionString
     *            Dimension string in the form of "number,number".
     * @return Parsed dimension.
     */
    public static Dimension getDimension(final String dimensionString) {
        Dimension dim = new Dimension();

        String[] result = dimensionString.split(",", 2);
        if (result.length != 2) {
            throw new IllegalStateException("Invalid dimension string.");
        }
        dim.setSize(Double.parseDouble(result[0].trim()),
                Double.parseDouble(result[1].trim()));

        return dim;
    }
}
