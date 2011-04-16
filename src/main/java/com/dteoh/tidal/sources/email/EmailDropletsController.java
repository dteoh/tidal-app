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

package com.dteoh.tidal.sources.email;

import static com.dteoh.tidal.util.EDTUtils.outsideEDT;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.jdesktop.application.ResourceMap;
import org.slf4j.Logger;

import com.dteoh.tidal.configuration.SaveConfigurable;
import com.dteoh.tidal.controllers.ViewManager;
import com.dteoh.tidal.exceptions.DropletCreationException;
import com.dteoh.tidal.exceptions.DropletInitException;
import com.dteoh.tidal.guice.InjectLogger;
import com.dteoh.tidal.id.ID;
import com.dteoh.tidal.sources.SetupDroplet;
import com.dteoh.tidal.sources.email.impl.ImapDroplet;
import com.dteoh.tidal.sources.email.models.EmailSettings;
import com.dteoh.tidal.sources.email.models.Protocol;
import com.dteoh.tidal.sources.email.views.EmailDropletSetup;
import com.dteoh.tidal.util.EDTUtils;
import com.dteoh.treasuremap.ResourceMaps;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import foxtrot.AsyncTask;
import foxtrot.AsyncWorker;
import foxtrot.Job;
import foxtrot.Worker;

/**
 * This controller is used to manage email droplets.
 * 
 * @author Douglas Teoh
 * 
 */
public final class EmailDropletsController implements SetupDroplet, EmailsController {

    /** Logger for this class. */
    @InjectLogger
    private Logger logger;

    /** Resource bundle for this class. */
    private static final ResourceMap BUNDLE = new ResourceMaps(EmailDropletsController.class).build();

    /** Email update schedule. */
    private static final long UPDATE_START_DELAY = TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS);
    private static final long UPDATE_SCHEDULE = TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES);

    /** Mapping between identifiers and email droplets. */
    private final Map<ID, AbstractEmailDroplet> droplets;

    /** Used to register droplets for settings serialization. */
    @Inject
    private SaveConfigurable saveConfig;

    /** View used for displaying droplet updates. */
    @Inject
    private ViewManager viewManager;

    /** View used for setting up new email droplets. */
    private EmailDropletSetup setupView;

    /** Used to schedule periodic email updates. */
    private Timer scheduler;

    /**
     * Creates a new email droplets controller.
     */
    @Inject
    private EmailDropletsController() {
        droplets = Maps.newHashMap();

        Runnable swingTask = new Runnable() {
            @Override
            public void run() {
                setupView = new EmailDropletSetup();
                setupView.addProtocols(Protocol.values());
            }
        };
        SwingUtilities.invokeLater(swingTask);
    }

    /**
     * Given an EmailSettings object, create and add the appropriate
     * implementation of an AbstractEmailDroplet.
     * 
     * @param emailSettings
     * @throws DropletCreationException
     *             If the given email settings are incompatible with any of the
     *             AbstractEmailDroplet implementations.
     * @return the created AbstractEmailDroplet.
     */
    public AbstractEmailDroplet addEmailDroplet(final EmailSettings emailSettings) throws DropletCreationException {
        outsideEDT();

        synchronized (this) {
            // Determine what type of droplet to build based on the given
            // protocol.
            final Protocol protocol = emailSettings.getProtocol();
            if (protocol == Protocol.imap || protocol == Protocol.imaps) {
                // IMAP(S) protocol
                final ImapDroplet imapsDroplet = ImapDroplet.create(emailSettings);

                // Initialize the droplet
                try {
                    imapsDroplet.init();
                } catch (DropletInitException e) {
                    throw new DropletCreationException("Could not initialize imap droplet", e);
                }

                imapsDroplet.setEmailsController(this);
                droplets.put(imapsDroplet.getIdentifier(), imapsDroplet);
                saveConfig.addConfigurable(imapsDroplet);

                EDTUtils.runOnEDT(new Runnable() {
                    @Override
                    public void run() {
                        viewManager.displayView(imapsDroplet.getDropletView());
                    }
                });

                return imapsDroplet;
            } else {
                // Unknown/unsupported protocols.
                throw new DropletCreationException("Unknown protocol: " + protocol);
            }
        }
    }

    /**
     * Removes and destroys a droplet being managed by the controller.
     * 
     * @param dropletID
     *            identifier of the droplet being removed.
     * @return true if the droplet exists and is destroyed, false otherwise.
     */
    public boolean destroyDroplet(final ID dropletID) {
        synchronized (this) {
            if (SwingUtilities.isEventDispatchThread()) {
                boolean result = (Boolean) Worker.post(new Job() {
                    @Override
                    public Object run() {
                        final AbstractEmailDroplet abstractEmailDroplet = droplets.remove(dropletID);
                        if (abstractEmailDroplet != null) {
                            abstractEmailDroplet.destroy();
                            saveConfig.removeConfigurable(abstractEmailDroplet);

                            EDTUtils.runOnEDT(new Runnable() {
                                @Override
                                public void run() {
                                    viewManager.removeView(abstractEmailDroplet.getDropletView());
                                }
                            });
                            return true;
                        }
                        return false;
                    }
                });
                return result;
            }

            // Should be a mirror of the posted job.
            final AbstractEmailDroplet abstractEmailDroplet = droplets.remove(dropletID);
            if (abstractEmailDroplet != null) {
                abstractEmailDroplet.destroy();
                saveConfig.removeConfigurable(abstractEmailDroplet);
                EDTUtils.runOnEDT(new Runnable() {
                    @Override
                    public void run() {
                        viewManager.removeView(abstractEmailDroplet.getDropletView());
                    }
                });
                return true;
            }
            return false;
        }
    }

    /**
     * Schedules email updates.
     */
    public void schedule() {
        if (scheduler == null) {
            // Daemon thread.
            scheduler = new Timer(true);
        } else {
            // Stop currently executing tasks.
            scheduler.cancel();
            scheduler = new Timer(true);
        }

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                for (final AbstractEmailDroplet d : droplets.values()) {
                    new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            d.update();
                            return null;
                        }

                        @Override
                        protected void done() {
                            try {
                                get();
                            } catch (Exception e) {
                                logger.error("Update error", e);
                            }
                        }
                    }.execute();

                }
            }
        };

        scheduler.schedule(task, UPDATE_START_DELAY, UPDATE_SCHEDULE);
    }

    /**
     * Pause scheduled email updates. Resume by calling {@link #schedule()}.
     */
    public void pause() {
        if (scheduler != null) {
            scheduler.cancel();
            scheduler = null;
        }
    }

    @Override
    public JComponent getSetupView() {
        return setupView;
    }

    @Override
    public Icon getSetupIcon() {
        outsideEDT();
        Icon setupIcon = BUNDLE.getImageIcon("email.image");
        return setupIcon;
    }

    @Override
    public void cancelSetup() {
        Runnable swingTask = new Runnable() {
            @Override
            public void run() {
                setupView.clearFields();
            }
        };
        SwingUtilities.invokeLater(swingTask);
    }

    @Override
    public boolean createDropletFromSetup() {
        final EmailSettings settings = setupView.getSettings();

        boolean result = (Boolean) Worker.post(new Job() {
            @Override
            public Object run() {
                try {
                    logger.debug("Creating droplet from setup");
                    final AbstractEmailDroplet d = EmailDropletsController.this.addEmailDroplet(settings);

                    if (SwingUtilities.isEventDispatchThread()) {
                        AsyncWorker.post(new AsyncTask() {

                            @Override
                            public Object run() throws Exception {
                                d.update();
                                return null;
                            }

                            @Override
                            public void success(final Object arg0) {
                            }

                            @Override
                            public void failure(final Throwable arg0) {
                            }
                        });
                    } else {
                        d.update();
                    }
                    logger.debug("Sucessfully created droplet");
                    return true;
                } catch (DropletCreationException e) {
                    logger.error("Failed to create droplet", e);
                }
                return false;
            }
        });

        return result;
    }
}
