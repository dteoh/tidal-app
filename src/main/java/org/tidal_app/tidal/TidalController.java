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
import java.awt.image.BufferedImage;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tidal_app.tidal.configuration.ConfigurationController;
import org.tidal_app.tidal.events.views.AccessViewEvent;
import org.tidal_app.tidal.events.views.AccessViewListener;
import org.tidal_app.tidal.exceptions.DropletCreationException;
import org.tidal_app.tidal.sources.email.EmailDropletsController;
import org.tidal_app.tidal.sources.email.models.EmailSettings;
import org.tidal_app.tidal.views.AccessView;
import org.tidal_app.tidal.views.models.DropletModel;
import org.tidal_app.tidal.views.swing.DropShadowPanel;
import org.tidal_app.tidal.views.swing.TiledImagePanel;

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

    private final static Logger LOGGER = LoggerFactory
            .getLogger(TidalController.class);

    /** Views */
    /** This is the main application frame */
    private JFrame mainFrame;
    /** This is the main application frame's panel */
    private JPanel mainFramePanel;
    /** This is the application's main panel */
    private TiledImagePanel mainApplicationView;

    /** Controllers */
    private final MenuBarController menuBarController;
    private final DropletsViewController dropletsViewController;
    private final ConfigurationController configurationController;
    private final EmailDropletsController emailDropletsController;

    public TidalController(
            final ConfigurationController configurationController,
            final EmailDropletsController emailDropletsController) {
        this.configurationController = configurationController;
        this.emailDropletsController = emailDropletsController;
        menuBarController = new MenuBarController();
        dropletsViewController = new DropletsViewController();
        initView();
    }

    private void initView() {
        final Runnable swingTask = new Runnable() {
            @Override
            public void run() {
                // Make our application frame.
                final JFrame mainFrame = new JFrame();
                mainFrame.setTitle("Tidal");
                mainFrame
                        .setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                mainFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(final WindowEvent e) {
                        exitHandler();
                    }
                });

                mainFramePanel = new JPanel(new CardLayout());

                mainFrame.add(mainFramePanel);

                // START: Set up the AccessView panel

                final TiledImagePanel accessViewPanel = new TiledImagePanel();
                accessViewPanel
                        .setLayout(new MigLayout("", "push[center]push"));

                BufferedImage backgroundImage = null;
                try {
                    backgroundImage = (BufferedImage) Worker.post(new Task() {
                        @Override
                        public Object run() throws Exception {
                            return ImageIO.read(getClass().getResource(
                                    "background.png"));
                        }
                    });
                } catch (final Exception e) {
                    LOGGER.error("Error loading image", e);
                }

                accessViewPanel.setBackground(new Color(90, 100, 115));
                accessViewPanel.setBackground(backgroundImage);

                final AccessView av = new AccessView();
                av.addAccessViewListener(TidalController.this);

                boolean isFirstRun = true;
                try {
                    isFirstRun = (Boolean) Worker.post(new Task() {
                        @Override
                        public Object run() {
                            return Boolean.valueOf(isFirstRun());
                        }
                    });
                } catch (final Exception e) {
                }

                if (isFirstRun) {
                    av.showFirstRun();
                } else {
                    av.showLogin();
                }

                accessViewPanel.add(av, "w 33%!");

                mainFramePanel.add(accessViewPanel, "ACCESS_VIEW");

                // END: Set up the AccessView panel

                // START: Set up the main application view.

                mainApplicationView = new TiledImagePanel();
                mainApplicationView.setLayout(new MigLayout("ins 0, wrap",
                        "[grow]", "[grow]"));
                mainApplicationView.setBackground(new Color(90, 100, 115));
                mainApplicationView.setBackground(backgroundImage);

                final DropShadowPanel menuBarPanel = new DropShadowPanel(6,
                        0.5F);
                menuBarPanel.setLayout(new MigLayout("", "0[grow]0", "0[]"));
                menuBarPanel.add(menuBarController.getView(), "growx");
                mainApplicationView.add(menuBarPanel, "pushx, growx, north");

                mainApplicationView.add(dropletsViewController.getView(),
                        "pushx, growx, north");

                mainFramePanel.add(mainApplicationView, "MAIN_VIEW");

                // END: Set up the main application view.

                final JScrollPane mainScrollPane = new JScrollPane(
                        mainFramePanel);
                mainScrollPane.setBorder(BorderFactory.createEmptyBorder());
                mainScrollPane
                        .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                mainScrollPane
                        .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                mainFrame.add(mainScrollPane);

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
                    configurationController.saveDropletSettings();
                    return null;
                }
            });
        } catch (final Exception e) {
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
     * 
     * @see
     * org.tidal_app.tidal.events.views.AccessViewListener#loginAttempted(org
     * .tidal_app.tidal.events.views.AccessViewEvent)
     */
    @Override
    public void loginAttempted(final AccessViewEvent evt) {
        final boolean passwordOK = (Boolean) Worker.post(new Job() {
            @Override
            public Object run() {
                return configurationController.authorize(evt.getPassword());
            }
        });

        if (passwordOK) {
            new SwingWorker<Void, DropletModel>() {
                @Override
                protected void process(
                        final List<DropletModel> dropletModelChunks) {
                    dropletsViewController
                            .updateDropletViews(dropletModelChunks);
                }

                @Override
                protected Void doInBackground() throws Exception {
                    final Iterable<Object> dropletSettings = configurationController
                            .loadDropletSettings();

                    for (final Object settings : dropletSettings) {
                        if (settings instanceof EmailSettings) {
                            final EmailSettings emailSettings = (EmailSettings) settings;
                            try {
                                emailDropletsController
                                        .addEmailDroplet(emailSettings);
                                publish(emailDropletsController
                                        .getDropletModel(emailSettings
                                                .getUsername()));
                            } catch (final DropletCreationException e) {
                                LOGGER.error("Cannot create droplet", e);
                            }
                        }
                    }
                    return null;
                }
            }.execute();

            final CardLayout cards = (CardLayout) mainFramePanel.getLayout();
            cards.show(mainFramePanel, "MAIN_VIEW");

            // TODO start timing thread.
        } else {
            final AccessView accessView = (AccessView) evt.getSource();
            accessView.displayMessage("Incorrect password.");
        }
    }

    /*
     * (non-Javadoc)
     * 
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
        } catch (final Exception e) {
            LOGGER.error("Setup password error", e);
        }

        if (passwordOK) {
            final CardLayout cards = (CardLayout) mainFramePanel.getLayout();
            cards.show(mainFramePanel, "MAIN_VIEW");
        } else {
            final AccessView accessView = (AccessView) evt.getSource();
            accessView.displayMessage("Password cannot be blank.");
        }
    }

}
