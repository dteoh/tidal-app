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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import org.tidal_app.tidal.util.ResourceUtils;
import org.tidal_app.tidal.views.events.MenuBarViewEvent;
import org.tidal_app.tidal.views.events.MenuBarViewListener;
import org.tidal_app.tidal.views.swing.GradientPanel;

import com.google.common.collect.Sets;

/**
 * Used to create a custom menu bar.
 * 
 * @author Douglas Teoh
 */
public final class MenuBarView extends GradientPanel {

    private static final ResourceBundle BUNDLE = ResourceBundle
            .getBundle(MenuBarView.class.getName());

    /** Colors */
    private static final Color APP_NAME_FOREGROUND = new Color(255, 255, 255);
    private static final Color APP_VERSION_FOREGROUND = new Color(176, 176, 176);
    private static final Color BAR_BOTTOM_COLOR = new Color(0, 55, 125);
    private static final Color BAR_TOP_COLOR = new Color(0, 100, 175);
    private static final Color BAR_BORDER_COLOR = new Color(0, 35, 110);

    private final Set<MenuBarViewListener> listeners;

    public MenuBarView() {
        super(BAR_TOP_COLOR, BAR_BOTTOM_COLOR);
        listeners = Sets.newHashSet();
        initView();
    }

    /**
     * Initialize the view.
     */
    private void initView() {
        inEDT();

        setLayout(new MigLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BAR_BORDER_COLOR));

        final JButton menuButton = new JButton();
        menuButton.setName("MenuBarViewMenuButton");
        menuButton.setContentAreaFilled(false);
        menuButton.setSelected(false);
        try {
            menuButton.setIcon(new ImageIcon(ResourceUtils.getImage(getClass(),
                    BUNDLE.getString("menuButton.icon"))));
        } catch (IOException e) {
        }
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                handleMenuButtonAction(e);
            }
        });
        add(menuButton, "w 32!, h 32!");

        // Application name
        final JLabel appName = new JLabel(BUNDLE.getString("appName.text"));
        appName.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        appName.setForeground(APP_NAME_FOREGROUND);
        add(appName);

        // TODO: Externalize application version number
        final JLabel appVersion = new JLabel(
                BUNDLE.getString("appVersion.text"));
        appVersion.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 22));
        appVersion.setForeground(APP_VERSION_FOREGROUND);
        add(appVersion);
    }

    private void handleMenuButtonAction(final ActionEvent evt) {
        MenuBarViewEvent newEvent = new MenuBarViewEvent(this);
        for (MenuBarViewListener listener : listeners) {
            listener.menuButtonClicked(newEvent);
        }
    }

    public void addMenuBarViewListener(final MenuBarViewListener listener) {
        listeners.add(listener);
    }

    public void removeMenuBarViewListener(final MenuBarViewListener listener) {
        listeners.remove(listener);
    }

}
