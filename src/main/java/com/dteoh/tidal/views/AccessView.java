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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.application.ResourceMap;

import com.dteoh.tidal.views.events.AccessViewEvent;
import com.dteoh.tidal.views.events.AccessViewListener;
import com.dteoh.tidal.views.swing.DropShadowPanel;
import com.dteoh.tidal.views.swing.GradientPanel;
import com.dteoh.treasuremap.ResourceMaps;
import com.google.common.collect.Lists;

/**
 * This view displays a login-style screen.
 * 
 * @author Douglas Teoh
 */
public final class AccessView extends DropShadowPanel {

    /** Resource bundle for this class. */
    private static final ResourceMap BUNDLE = new ResourceMaps(AccessView.class)
            .build();

    /** Heading label. */
    private JLabel heading;
    /** Information label. */
    private JLabel information;

    /** Password field. */
    private JPasswordField passwordField;
    /** Password confirmation field. */
    private JPasswordField confirmationField;

    /** Unlock/Login button. */
    private JButton unlockButton;

    private Action unlockAction;

    private final List<AccessViewListener> listeners;

    /**
     * Creates the login screen.
     */
    public AccessView() {
        super(BUNDLE.getInteger("shadow.size"), BUNDLE
                .getFloat("shadow.opacity"));
        listeners = Lists.newLinkedList();
        initView();
    }

    /**
     * Initialize the view.
     */
    private void initView() {
        inEDT();

        setLayout(new MigLayout("", "[grow]", "[]0[]0[]"));

        // Construct our header panel and header contents

        final GradientPanel headerPanel = new GradientPanel(
                BUNDLE.getColor("header.top.color"),
                BUNDLE.getColor("header.bottom.color"));
        headerPanel.setLayout(new MigLayout());

        heading = new JLabel();
        heading.setForeground(BUNDLE.getColor("heading.foreground"));
        heading.setFont(BUNDLE.getFont("heading.font"));
        heading.setName("AccessViewHeading");

        headerPanel.add(heading);

        add(headerPanel, "growx, wrap");

        // Construct our information panel
        final GradientPanel infoPanel = new GradientPanel(
                BUNDLE.getColor("infoPanel.top.color"),
                BUNDLE.getColor("infoPanel.bottom.color"));
        infoPanel.setLayout(new MigLayout());
        information = new JLabel();
        information.setName("AccessViewInformation");
        infoPanel.add(information);

        add(infoPanel, "growx, wrap");
    }

    /**
     * Display the first run screen.
     */
    @SuppressWarnings("serial")
    public void showFirstRun() {
        inEDT();

        heading.setText(BUNDLE.getString("heading.firstrun.text"));
        information.setText(BUNDLE.getString("information.firstrun.text"));

        passwordField = new JPasswordField();
        passwordField.setName("AccessViewPasswordField");
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                // Want to unlock the confirm button if the confirmation field
                // matches the password field.
                if (Arrays.equals(confirmationField.getPassword(),
                        passwordField.getPassword())) {
                    unlockButton.setEnabled(true);
                } else {
                    unlockButton.setEnabled(false);
                }
            }
        });
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                confirmationField.requestFocusInWindow();
            }
        });

        confirmationField = new JPasswordField();
        confirmationField.setName("AccessViewConfirmationField");
        confirmationField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                // Want to unlock the confirm button if the confirmation field
                // matches the password field.
                if (Arrays.equals(confirmationField.getPassword(),
                        passwordField.getPassword())) {
                    unlockAction.setEnabled(true);
                } else {
                    unlockAction.setEnabled(false);
                }
            }
        });
        confirmationField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                unlockButton.doClick();
            }
        });

        unlockButton = new JButton();
        unlockButton.setName("AccessViewUnlockButton");
        unlockAction = new AbstractAction(
                BUNDLE.getString("unlockAction.firstrun.name")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                handleUnlockButtonAction(e, true);
            }
        };
        unlockAction.setEnabled(false);
        unlockButton.setAction(unlockAction);

        // Construct input panel.

        final JPanel inputPanel = new JPanel(new MigLayout());
        inputPanel.add(passwordField, "pushx, growx, wrap");
        inputPanel.add(confirmationField, "pushx, growx");
        inputPanel.add(unlockButton, "wrap");

        add(inputPanel, "pushx, growx, wrap");
    }

    /**
     * Display the login screen.
     */
    @SuppressWarnings("serial")
    public void showLogin() {
        inEDT();

        heading.setText(BUNDLE.getString("heading.login.text"));
        information.setText(BUNDLE.getString("information.login.text"));

        passwordField = new JPasswordField();
        passwordField.setName("AccessViewPasswordField");
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                unlockButton.doClick();
            }
        });

        unlockButton = new JButton();
        unlockAction = new AbstractAction(
                BUNDLE.getString("unlockAction.login.name")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                handleUnlockButtonAction(e, false);
            }
        };
        unlockButton.setAction(unlockAction);
        unlockButton.setName("AccessViewUnlockButton");

        // Construct input panel.
        final JPanel inputPanel = new JPanel(new MigLayout());
        inputPanel.add(passwordField, "pushx, growx");
        inputPanel.add(unlockButton, "wrap");

        add(inputPanel, "pushx, growx, wrap");
    }

    /**
     * Disable the login action.
     */
    public void disableLogin() {
        inEDT();
        if (unlockAction != null) {
            unlockAction.setEnabled(false);
            passwordField.setEnabled(false);
        }
    }

    /**
     * Enable the login action.
     */
    public void enableLogin() {
        inEDT();
        if (unlockAction != null) {
            unlockAction.setEnabled(true);
            passwordField.setEnabled(true);
        }
    }

    /**
     * Displays the given message in the information label.
     * 
     * @param message
     */
    public void displayMessage(final String message) {
        inEDT();
        information.setText(message);
    }

    /**
     * Event handler for the unlock button.
     * 
     * @param e
     * @param firstRun
     */
    private void handleUnlockButtonAction(final ActionEvent e,
            final boolean firstRun) {
        // Clear the input.
        Arrays.fill(passwordField.getPassword(), '\0');
        if (confirmationField != null) {
            Arrays.fill(confirmationField.getPassword(), '\0');
        }

        // FIXME This may not be the best way of passing passwords around.
        final String password = new String(passwordField.getPassword());
        if (firstRun) {
            fireSetupPasswordEvent(password);
        } else {
            fireLoginAttemptedEvent(password);
        }
    }

    /**
     * Add a listener interested in events from this view.
     * 
     * @param listener
     */
    public void addAccessViewListener(final AccessViewListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove the given listener.
     * 
     * @param listener
     */
    public void removeAccessViewListener(final AccessViewListener listener) {
        listeners.remove(listener);
    }

    /**
     * Inform listeners about the login attempt.
     * 
     * @param password
     */
    private void fireLoginAttemptedEvent(final String password) {
        inEDT();
        final AccessViewEvent event = new AccessViewEvent(this, password);
        for (final AccessViewListener listener : listeners) {
            listener.loginAttempted(event);
        }
    }

    /**
     * Inform listeners about the password setup event.
     * 
     * @param password
     */
    private void fireSetupPasswordEvent(final String password) {
        inEDT();
        final AccessViewEvent event = new AccessViewEvent(this, password);
        for (final AccessViewListener listener : listeners) {
            listener.setupPassword(event);
        }
    }

}
