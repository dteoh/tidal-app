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

package com.dteoh.tidal.platform.windows;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.BaseTSD.LONG_PTR;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.Ole32Util;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HINSTANCE;
import com.sun.jna.platform.win32.WinDef.HMENU;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * This interface is for binding with Windows Vista or above versions of
 * User32.dll. What we want to do is to be able to register for power setting
 * notifications.
 * 
 * @author Douglas Teoh
 */
public interface VistaUser32 extends StdCallLibrary {

    static final VistaUser32 INSTANCE = (VistaUser32) Native.loadLibrary("user32", VistaUser32.class,
            W32APIOptions.DEFAULT_OPTIONS);

    static final GUID GUID_SYSTEM_AWAYMODE = Ole32Util.getGUIDFromString("{98a7f580-01f7-48aa-9c0f-44352c29e5C0}");

    /**
     * Sets a new address for the window procedure.
     * 
     * @see href http://msdn.microsoft.com/en-us/library/ms633591(v=vs.85).aspx
     */
    static final int GWL_WNDPROC = -4;

    /**
     * Message ID for power-management events.
     * 
     * @see href http://msdn.microsoft.com/en-us/library/aa373247(v=VS.85).aspx
     */
    static final int WM_POWERBROADCAST = 0x218;

    /**
     * Event ID for when the system is about to enter a suspended state.
     * 
     * @see href http://msdn.microsoft.com/en-us/library/aa372721(v=VS.85).aspx
     */
    static final WPARAM PBT_APMSUSPEND = new WPARAM(0x4);

    /**
     * Event ID for when the system has resumed operation after being suspended.
     * 
     * @see href http://msdn.microsoft.com/en-us/library/aa372720(v=VS.85).aspx
     */
    static final WPARAM PBT_APMRESUMESUSPEND = new WPARAM(0x7);

    /**
     * Registers the application to receive power setting notifications for the
     * specific power setting event.
     * 
     * @param hRecipient
     *            This should be a window handle.
     * @param PowerSettingGuid
     * @param Flags
     *            This should be 0 because Tidal is an interactive app.
     * 
     * @return A notification handle for unregistering for power notifications.
     *         If the function fails, the return value is NULL.
     * 
     * @see Native#getWindowPointer(java.awt.Window)
     * @see href http://msdn.microsoft.com/en-us/library/aa373196(v=VS.85).aspx
     */
    Pointer RegisterPowerSettingNotification(HANDLE hRecipient, GUID PowerSettingGuid, int Flags);

    /**
     * Unregisters the power setting notification.
     * 
     * @param Handle
     *            The pointer returned by
     *            {@link #RegisterPowerSettingNotification(HANDLE, Pointer, DWORD)}
     * 
     * @return True if the function succeeds, false otherwise.
     * 
     * @see href http://msdn.microsoft.com/en-us/library/aa373237(VS.85).aspx
     */
    boolean UnregisterPowerSettingNotification(Pointer Handle);

    /**
     * Creates an overlapped, pop-up, or child window with an extended window
     * style.
     * 
     * @see href http://msdn.microsoft.com/en-us/library/ms632680(VS.85).aspx
     */
    HWND CreateWindowEx(int styleEx, String className, String windowName, int style, int x, int y, int width,
            int height, HWND hWndParent, HMENU hMenu, HINSTANCE hInstance, Pointer lpParam);

    /**
     * Destroys the specified window.
     * 
     * @param hWnd
     *            A handle to the window to be destroyed.
     * 
     * @return True if the function succeeds, false otherwise.
     * 
     * @see href http://msdn.microsoft.com/en-us/library/ms632682(VS.85).aspx
     */
    boolean DestroyWindow(HWND hWnd);

    /**
     * Changes an attribute of the specified window. The function also sets a
     * value at the specified offset in the extra window memory.
     * 
     * @see href http://msdn.microsoft.com/en-us/library/ms644898(v=VS.85).aspx
     */
    LONG_PTR SetWindowLongPtr(HWND hWnd, int nIndex, WNDPROC dwNewLong);

    /**
     * Calls the default window procedure to provide default processing for any
     * window messages that an application does not process. This function
     * ensures that every message is processed. DefWindowProc is called with the
     * same parameters received by the window procedure.
     * 
     * @see href http://msdn.microsoft.com/en-us/library/ms633572(VS.85).aspx
     */
    int DefWindowProc(HWND hWnd, int uMsg, WPARAM wParam, LPARAM lParam);

    /**
     * An application-defined function that processes messages sent to a window.
     * 
     * @see href http://msdn.microsoft.com/en-us/library/ms633573(VS.85).aspx
     */
    static interface WNDPROC extends StdCallCallback {
        int WndProc(HWND hWnd, int uMsg, WPARAM wParam, LPARAM lParam);
    }

}
