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

package org.tidal_app.tidal.sources.email;

import static org.tidal_app.tidal.util.EDTUtils.outsideEDT;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.jdesktop.application.ResourceMap;
import org.slf4j.Logger;
import org.tidal_app.tidal.configuration.SaveConfigurable;
import org.tidal_app.tidal.controllers.ViewManager;
import org.tidal_app.tidal.exceptions.DropletCreationException;
import org.tidal_app.tidal.exceptions.DropletInitException;
import org.tidal_app.tidal.guice.InjectLogger;
import org.tidal_app.tidal.id.ID;
import org.tidal_app.tidal.sources.SetupDroplet;
import org.tidal_app.tidal.sources.email.impl.ImapDroplet;
import org.tidal_app.tidal.sources.email.models.EmailSettings;
import org.tidal_app.tidal.sources.email.models.Protocol;
import org.tidal_app.tidal.sources.email.views.EmailDropletSetup;
import org.tidal_app.tidal.util.EDTUtils;

import com.dteoh.treasuremap.ResourceMaps;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import foxtrot.Job;
import foxtrot.Worker;

/**
 * This controller is used to manage email droplets.
 * 
 * @author Douglas Teoh
 * 
 */
public final class EmailDropletsController implements SetupDroplet {

    /** Logger for this class. */
    @InjectLogger
    private Logger logger;

    /** Resource bundle for this class. */
    private static final ResourceMap BUNDLE = new ResourceMaps(
            EmailDropletsController.class).build();

    /** Email update schedule. */
    private static final long UPDATE_SCHEDULE = TimeUnit.MILLISECONDS.convert(
            5, TimeUnit.MINUTES);

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
    public AbstractEmailDroplet addEmailDroplet(
            final EmailSettings emailSettings) throws DropletCreationException {
        outsideEDT();

        synchronized (this) {
            // Determine what type of droplet to build based on the given
            // protocol.
            final Protocol protocol = emailSettings.getProtocol();
            if (protocol == Protocol.imap || protocol == Protocol.imaps) {
                // imap(S) protocol
                final ImapDroplet imapsDroplet = ImapDroplet
                        .create(emailSettings);

                // Disallow overwriting existing mappings.
                if (isManaging(emailSettings.getUsername())) {
                    throw new DropletCreationException(
                            "Duplicate AbstractEmailDroplet for "
                                    + emailSettings.getUsername());
                }

                // Initialize the droplet
                try {
                    imapsDroplet.init();
                } catch (DropletInitException e) {
                    throw new DropletCreationException(
                            "Could not initialize imap droplet", e);
                }

                droplets.put(imapsDroplet.getIdentifier(), imapsDroplet);
                saveConfig.addConfigurable(imapsDroplet);

                EDTUtils.runOnEDT(new Runnable() {
                    @Override
                    public void run() {
                        viewManager.displayView(imapsDroplet.getDropletView());
                    }
                });

                // Get any emails from the newly created droplet and display to
                // the user.
                imapsDroplet.update();

                return imapsDroplet;
            } else {
                // Unknown/unsupported protocols.
                throw new DropletCreationException("Unknown protocol: "
                        + protocol);
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
    public boolean destroyEmailDroplet(final ID dropletID) {
        outsideEDT();

        synchronized (this) {
            final AbstractEmailDroplet abstractEmailDroplet = droplets
                    .remove(dropletID);
            if (abstractEmailDroplet != null) {
                abstractEmailDroplet.destroy();
                return true;
            }
        }
        return false;
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
                for (AbstractEmailDroplet d : droplets.values()) {
                    d.update();
                }
            }
        };
        scheduler.schedule(task, UPDATE_SCHEDULE, UPDATE_SCHEDULE);
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
                    EmailDropletsController.this.addEmailDroplet(settings);
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

    /**
     * Check if we are already managing an email droplet with the same username.
     * 
     * @param username
     *            Username to check.
     * @return true if already managing a droplet with the same email address,
     *         false otherwise.
     */
    private boolean isManaging(final String username) {
        for (AbstractEmailDroplet droplet : droplets.values()) {
            if (droplet.getUsername().equals(username) == true) {
                return true;
            }
        }

        return false;
    }

}
