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

import java.util.List;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tidal_app.tidal.platform.windows.VistaUser32.WNDPROC;
import org.tidal_app.tidal.platform.windows.events.PowerStateListener;

import com.google.common.collect.Lists;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.BaseTSD.LONG_PTR;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.WPARAM;

/**
 * This class listens to system suspend and resume events on Windows.
 */
public final class SuspendAndResumeListener {

    private static final Logger logger = LoggerFactory.getLogger(SuspendAndResumeListener.class);

    private HWND broadcastWindow;
    private Pointer notificationHandle;
    private WNDPROC proc;

    /** Listeners interested in power events. */
    private final List<PowerStateListener> listeners;

    /**
     * Creates a new listener for Windows suspend and resume events.
     * 
     * @param broadcastFrame
     *            A visible frame capable of receiving broadcast events. Must
     *            not be null.
     */
    public SuspendAndResumeListener(final JFrame broadcastFrame) {
        if (broadcastFrame == null) {
            throw new NullPointerException();
        }

        listeners = Lists.newLinkedList();

        final VistaUser32 user32 = VistaUser32.INSTANCE;

        broadcastWindow = new HWND(Native.getWindowPointer(broadcastFrame));
        notificationHandle = user32.RegisterPowerSettingNotification(broadcastWindow, VistaUser32.GUID_SYSTEM_AWAYMODE,
                0);
        if (notificationHandle == null) {
            logger.error("Failed to register for PowerSettingNotification");
            return;
        }

        proc = new WNDPROC() {
            @Override
            public int WndProc(final HWND hWnd, final int uMsg, final WPARAM wParam, final LPARAM lParam) {
                if (uMsg == VistaUser32.WM_POWERBROADCAST) {
                    if (VistaUser32.PBT_APMSUSPEND.equals(wParam)) {
                        // Notify listeners of impending system sleep.
                        for (PowerStateListener l : listeners) {
                            l.suspend();
                        }
                        return 0;
                    }
                    if (VistaUser32.PBT_APMRESUMESUSPEND.equals(wParam)) {
                        // Notify listeners that system has resumed.
                        for (PowerStateListener l : listeners) {
                            l.resume();
                        }
                        return 0;
                    }
                }
                return user32.DefWindowProc(hWnd, uMsg, wParam, lParam);
            }
        };

        LONG_PTR ptr = user32.SetWindowLongPtr(broadcastWindow, VistaUser32.GWL_WNDPROC, proc);
        if (ptr.longValue() == 0) {
            logger.error("Failed to set new address of window procedure");
        }
    }

    /**
     * Add a listener interested in listening to suspend and resume events.
     * 
     * @param listener
     *            Listener to add. If null, nothing happens.
     */
    public void addPowerStateListener(final PowerStateListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * Unsubscribe the given listener from suspend and resume events.
     * 
     * @param listener
     *            Listener to unsubscribe. If null, nothing happens.
     */
    public void removePowerStateListener(final PowerStateListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    /**
     * Destroys this listener. All event listeners registered to this class are
     * also removed. The listener is no longer valid after calling this method.
     */
    public void destroy() {
        VistaUser32 user32 = VistaUser32.INSTANCE;

        listeners.clear();

        user32.UnregisterPowerSettingNotification(notificationHandle);
        notificationHandle = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        destroy();
    }
}
