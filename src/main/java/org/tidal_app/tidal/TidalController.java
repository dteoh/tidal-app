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

package org.tidal_app.tidal;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tidal_app.tidal.configuration.ConfigurationController;
import org.tidal_app.tidal.events.views.AccessViewEvent;
import org.tidal_app.tidal.events.views.AccessViewListener;
import org.tidal_app.tidal.views.AccessView;

import foxtrot.Job;
import foxtrot.Task;
import foxtrot.Worker;

/**
 * Main application controller. This controller is responsible for adding new
 * accounts to the application and scheduling email checking. This controller is
 * also responsible for coordinating other controllers.
 * 
 * @author Douglas Teoh
 */
public class TidalController implements AccessViewListener {

    private final static Logger LOGGER =
        LoggerFactory.getLogger(TidalController.class);

    /** Views */
    /** This is the main application frame */
    private JFrame mainFrame;
    /** This is the main application frame's panel */
    private JPanel mainFramePanel;
    /** This is the application's main panel */
    private JPanel mainApplicationView;

    /** Controllers */
    private final MenuBarController menuBarController;
    private final DropletsController dropletsController;
    private final ConfigurationController configurationController;

    public TidalController() {
        menuBarController = new MenuBarController();
        dropletsController = new DropletsController();
        configurationController = new ConfigurationController();
        initView();
    }

    private void initView() {
        Runnable swingTask = new Runnable() {
            @Override
            public void run() {
                // Make our application frame.
                mainFrame = new JFrame() {
                    {
                        setTitle("Tidal");
                        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                        addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosing(final WindowEvent e) {
                                exitHandler();
                            }
                        });

                    }
                };

                mainFramePanel = new JPanel() {
                    {
                        setLayout(new CardLayout());
                    }
                };

                mainFrame.add(mainFramePanel);

                mainFramePanel.add(new AccessView() {
                    {
                        addAccessViewListener(TidalController.this);

                        boolean isFirstRun = true;
                        try {
                            isFirstRun = (Boolean) Worker.post(new Task() {
                                @Override
                                public Object run() {
                                    return Boolean.valueOf(isFirstRun());
                                }
                            });
                        } catch (Exception e) {
                        }

                        if (isFirstRun) {
                            showFirstRun();
                        } else {
                            showLogin();
                        }
                    }
                }, "ACCESS_VIEW");

                mainApplicationView = new JPanel() {
                    {
                        setLayout(new MigLayout("ins 0", "[grow]", "[grow]"));
                        setBackground(new Color(248, 248, 248));

                        add(menuBarController.getView(),
                                "pushx, growx, wrap, north");
                        add(dropletsController.getView(), "pushx, growx, north");
                    }
                };

                mainFramePanel.add(mainApplicationView, "MAIN_VIEW");

                mainFrame.add(new JScrollPane(mainFramePanel) {
                    {
                        setBorder(BorderFactory.createEmptyBorder());
                        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                    }
                });

                mainFrame.setVisible(true);
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            swingTask.run();
        } else {
            SwingUtilities.invokeLater(swingTask);
        }
    }

    /**
     * Application exit handler.
     */
    private void exitHandler() {
        try {
            Worker.post(new Task() {
                @Override
                public Object run() throws Exception {
                    configurationController.saveMainSettings();
                    return null;
                }
            });
        } catch (Exception e) {
            LOGGER.error("Error saving application settings", e);
        }
        System.exit(0);
    }

    /**
     * Determines if this is the first time the application is being used by
     * loading the configuration file.
     * 
     * @return true if this is the first run, false otherwise.
     */
    private boolean isFirstRun() {
        return !configurationController.loadMainSettings();
    }

    /*
     * (non-Javadoc)
     * @see
     * org.tidal_app.tidal.events.views.AccessViewListener#loginAttempted(org
     * .tidal_app.tidal.events.views.AccessViewEvent)
     */
    @Override
    public void loginAttempted(final AccessViewEvent evt) {
        boolean passwordOK = (Boolean) Worker.post(new Job() {
            @Override
            public Object run() {
                return configurationController.authorize(evt.getPassword());
            }
        });

        if (passwordOK) {
            // TODO unlock droplet settings, then show the main interface.
            CardLayout cards = (CardLayout) mainFramePanel.getLayout();
            cards.show(mainFramePanel, "MAIN_VIEW");

            // TODO start timing thread.
        } else {
            AccessView accessView = (AccessView) evt.getSource();
            accessView.displayMessage("Incorrect password.");
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.tidal_app.tidal.events.views.AccessViewListener#setupPassword(org
     * .tidal_app.tidal.events.views.AccessViewEvent)
     */
    @Override
    public void setupPassword(final AccessViewEvent evt) {
        // Try setting Tidal up with the given password.
        boolean passwordOK = false;
        try {
            passwordOK = (Boolean) Worker.post(new Task() {
                @Override
                public Object run() throws Exception {
                    configurationController.changeAuthorizationKey(evt
                            .getPassword());
                    return true;
                }
            });
        } catch (Exception e) {
            LOGGER.error("Setup password error", e);
        }

        if (passwordOK) {
            CardLayout cards = (CardLayout) mainFramePanel.getLayout();
            cards.show(mainFramePanel, "MAIN_VIEW");
        } else {
            AccessView accessView = (AccessView) evt.getSource();
            accessView.displayMessage("Password cannot be blank.");
        }
    }

}
