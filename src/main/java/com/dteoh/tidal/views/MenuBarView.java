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

package com.dteoh.tidal.views;

import static com.dteoh.tidal.util.EDTUtils.inEDT;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.application.ResourceMap;

import com.dteoh.tidal.views.events.MenuBarViewEvent;
import com.dteoh.tidal.views.events.MenuBarViewListener;
import com.dteoh.tidal.views.swing.GradientPanel;
import com.dteoh.treasuremap.ResourceMaps;
import com.google.common.collect.Sets;

/**
 * Used to create a custom menu bar.
 * 
 * @author Douglas Teoh
 */
public final class MenuBarView extends GradientPanel {

    private static final ResourceMap BUNDLE = new ResourceMaps(
            MenuBarView.class).build();

    /** Colors */
    private static final Color APP_NAME_FOREGROUND = BUNDLE
            .getColor("appName.foreground");
    private static final Color APP_VERSION_FOREGROUND = BUNDLE
            .getColor("appVersion.foreground");
    private static final Color BAR_BOTTOM_COLOR = BUNDLE
            .getColor("bottom.color");
    private static final Color BAR_TOP_COLOR = BUNDLE.getColor("top.color");
    private static final Color BAR_BORDER_COLOR = BUNDLE
            .getColor("border.color");

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
        menuButton.setIcon(BUNDLE.getImageIcon("menuButton.icon"));
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                handleMenuButtonAction(e);
            }
        });
        add(menuButton, "w 32!, h 32!");

        // Application name
        final JLabel appName = new JLabel(BUNDLE.getString("appName.text"));
        appName.setFont(BUNDLE.getFont("appName.font"));
        appName.setForeground(APP_NAME_FOREGROUND);
        add(appName);

        final JLabel appVersion = new JLabel(
                BUNDLE.getString("appVersion.text"));
        appVersion.setFont(BUNDLE.getFont("appVersion.font"));
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
