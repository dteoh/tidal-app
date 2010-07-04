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

package org.tidal_app.tidal.views;

import static org.tidal_app.tidal.util.EDTUtils.inEDT;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import org.tidal_app.tidal.views.swing.GradientPanel;

/**
 * Used to create a custom menu bar.
 * 
 * @author Douglas Teoh
 */
public final class MenuBarView extends GradientPanel {

    /*
     * Colors
     */
    private static final Color APP_NAME_FOREGROUND = new Color(255, 255, 255);
    private static final Color APP_VERSION_FOREGROUND = new Color(176, 176, 176);
    private static final Color BAR_BOTTOM_COLOR = new Color(0, 55, 125);
    private static final Color BAR_TOP_COLOR = new Color(0, 100, 175);

    public MenuBarView() {
        super(BAR_TOP_COLOR, BAR_BOTTOM_COLOR);
        initView();
    }

    /**
     * Initialize the view.
     */
    private void initView() {
        inEDT();

        setLayout(new MigLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0, 35,
                110)));

        // This button will act like a menu.
        // TODO Add custom icon, make custom events.
        add(new JButton(), "w 32!, h 32!");

        // Application name
        final JLabel appName = new JLabel("Tidal");
        appName.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        appName.setForeground(APP_NAME_FOREGROUND);
        add(appName);

        // TODO: Externalize application version number
        final JLabel appVersion = new JLabel("0.1-dev");
        appVersion.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 22));
        appVersion.setForeground(APP_VERSION_FOREGROUND);
        add(appVersion);
    }

}
