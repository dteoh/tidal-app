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

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.tidal_app.tidal.events.views.AccessViewEvent;
import org.tidal_app.tidal.events.views.AccessViewListener;
import org.tidal_app.tidal.views.swing.DropShadowPanel;
import org.tidal_app.tidal.views.swing.GradientPanel;

/**
 * This view displays a login-style screen.
 * 
 * @author Douglas Teoh
 */
public class AccessView extends DropShadowPanel {

    private JLabel heading;
    private JLabel information;

    private JPasswordField passwordField;
    private JPasswordField confirmationField;

    private JButton unlockButton;

    private final List<AccessViewListener> listeners;

    public AccessView() {
        super(6, 0.5F);
        listeners = new LinkedList<AccessViewListener>();
        initView();
    }

    /**
     * Initialize the view.
     */
    private void initView() {
        assert SwingUtilities.isEventDispatchThread();

        setLayout(new MigLayout("", "[grow]", "[]0[]0[]"));

        // Construct our header panel and header contents

        final GradientPanel headerPanel = new GradientPanel(new Color(0, 100,
                175), new Color(0, 55, 125));
        headerPanel.setLayout(new MigLayout());

        heading = new JLabel();
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        heading.setName("AccessViewHeading");

        headerPanel.add(heading);

        add(headerPanel, "growx, wrap");

        // Construct our information panel
        final GradientPanel infoPanel = new GradientPanel(new Color(235, 240,
                250), new Color(215, 225, 235));
        infoPanel.setLayout(new MigLayout());
        information = new JLabel();
        information.setName("AccessViewInformation");
        infoPanel.add(information);

        add(infoPanel, "growx, wrap");
    }

    /**
     * Display the first run screen.
     */
    public void showFirstRun() {
        assert SwingUtilities.isEventDispatchThread();

        // TODO externalise this text
        heading.setText("First run");
        information
                .setText("Create a password so that your Tidal settings are secured.");

        passwordField = new JPasswordField();
        passwordField.setName("AccessViewPasswordField");
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                // Want to unlock the confirm button if the
                // confirmation
                // field matches the password field.
                if (Arrays.equals(confirmationField.getPassword(),
                        passwordField.getPassword())) {
                    unlockButton.setEnabled(true);
                } else {
                    unlockButton.setEnabled(false);
                }
            }
        });

        confirmationField = new JPasswordField();
        confirmationField.setName("AccessViewConfirmationField");
        confirmationField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                // Want to unlock the confirm button if the
                // confirmation
                // field matches the password field.
                if (Arrays.equals(confirmationField.getPassword(),
                        passwordField.getPassword())) {
                    unlockButton.setEnabled(true);
                } else {
                    unlockButton.setEnabled(false);
                }
            }
        });

        unlockButton = new JButton();
        unlockButton.setName("AccessViewUnlockButton");
        unlockButton.setText("Confirm");
        unlockButton.setEnabled(false);
        unlockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                handleUnlockButtonAction(e, true);
            }
        });

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
    public void showLogin() {
        assert SwingUtilities.isEventDispatchThread();

        // TODO externalise this text
        heading.setText("Unlock Tidal");
        information.setText("Sign in with your Tidal password.");

        passwordField = new JPasswordField();
        passwordField.setName("AccessViewPasswordField");

        unlockButton = new JButton();
        unlockButton.setText("Unlock");
        unlockButton.setName("AccessViewUnlockButton");
        unlockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                handleUnlockButtonAction(e, false);
            }
        });

        // Construct input panel.
        final JPanel inputPanel = new JPanel(new MigLayout());
        inputPanel.add(passwordField, "pushx, growx");
        inputPanel.add(unlockButton, "wrap");

        add(inputPanel, "pushx, growx, wrap");
    }

    /**
     * Displays the given message in the information label.
     * 
     * @param message
     */
    public void displayMessage(final String message) {
        assert SwingUtilities.isEventDispatchThread();
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
        Arrays.fill(passwordField.getPassword(), '0');
        if (confirmationField != null) {
            Arrays.fill(confirmationField.getPassword(), '0');
        }

        // FIXME This isn't the best way of passing passwords around.
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
        assert SwingUtilities.isEventDispatchThread();
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
        assert SwingUtilities.isEventDispatchThread();
        final AccessViewEvent event = new AccessViewEvent(this, password);
        for (final AccessViewListener listener : listeners) {
            listener.setupPassword(event);
        }
    }

}
