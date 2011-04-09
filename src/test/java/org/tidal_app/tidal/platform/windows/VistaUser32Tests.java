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

package org.tidal_app.tidal.platform.windows;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;

/**
 * Some tests for the Windows Vista and above version of user32.dll bindings.
 * 
 * @author Douglas Teoh
 */
public class VistaUser32Tests {

    /**
     * Test creating and destroying a native window.
     */
    @Test
    public void testCreateAndDestroyWindowHandle() {
        VistaUser32 instance = VistaUser32.INSTANCE;

        HWND hWnd = instance.CreateWindowEx(0, "STATIC", "", 0, 0, 0, 0, 0, null, null, null, null);
        assertNotNull("Expecting window handle to be created.", hWnd);
        assertTrue("Failed to destroy window", instance.DestroyWindow(hWnd));
    }

    /**
     * Test bad registration.
     */
    @Test
    public void testRegisterPowerSettingNotification1() {
        VistaUser32 instance = VistaUser32.INSTANCE;

        HWND listeningWindow = instance.CreateWindowEx(0, "STATIC", "", 0, 0, 0, 0, 0, null, null, null, null);
        Pointer notificationHandle = instance.RegisterPowerSettingNotification(listeningWindow, null, 0);

        assertNull("Not expecting registration to succeed", notificationHandle);

        instance.DestroyWindow(listeningWindow);
    }

    /**
     * Test good registration and unregistration.
     */
    @Test
    public void testRegisterPowerSettingNotification2() {
        VistaUser32 instance = VistaUser32.INSTANCE;

        HWND listeningWindow = instance.CreateWindowEx(0, "STATIC", "", 0, 0, 0, 0, 0, null, null, null, null);

        Pointer notificationHandle = instance.RegisterPowerSettingNotification(listeningWindow,
                VistaUser32.GUID_SYSTEM_AWAYMODE, 0);

        assertNotNull("Expecting notification handle to be returned", notificationHandle);
        assertTrue("Failed to unregister from power setting notification",
                instance.UnregisterPowerSettingNotification(notificationHandle));

        instance.DestroyWindow(listeningWindow);
    }
}
