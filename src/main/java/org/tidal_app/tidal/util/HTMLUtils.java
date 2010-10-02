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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Utility class for working with HTML content.
 * 
 * @author Douglas Teoh
 */
public final class HTMLUtils {

    /**
     * Converts HTML to plaintext.
     * 
     * This method was from: http://goo.gl/Pfxv
     * 
     * @param html
     *            The HTML content to convert.
     */
    public static String html2text(final String html) {
        Document document = Jsoup.parse(html);
        document.select("br").append("\\n");
        document.select("p").prepend("\\n\\n");
        return document.text().replaceAll("\\\\n", "\n");
    }

}
