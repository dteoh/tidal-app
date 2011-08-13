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

package com.dteoh.tidal.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Utility class for networks.
 * 
 * @author Douglas Teoh
 */
public final class NetworkUtils {

    /**
     * Tests if the Internet is accessible by trying to perform a HTTP HEAD
     * request on Google.
     * 
     * @return true if the Internet is reachable, false otherwise.
     */
    public static boolean isGoogleReachable() {
        try {
            URL url = new URL("http://www.google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            Object headers = connection.getContent();
        } catch (UnknownHostException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
