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
import static org.tidal_app.tidal.util.ResourceUtils.getImage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.tidal_app.tidal.configuration.SaveConfigurable;
import org.tidal_app.tidal.exceptions.DropletCreationException;
import org.tidal_app.tidal.exceptions.DropletInitException;
import org.tidal_app.tidal.guice.InjectLogger;
import org.tidal_app.tidal.id.ID;
import org.tidal_app.tidal.sources.SetupDroplet;
import org.tidal_app.tidal.sources.email.impl.ImapDroplet;
import org.tidal_app.tidal.sources.email.models.EmailSettings;
import org.tidal_app.tidal.sources.email.views.EmailDropletSetup;
import org.tidal_app.tidal.views.DropletsView;

import com.google.common.collect.Lists;
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
    private static final ResourceBundle BUNDLE = ResourceBundle
            .getBundle(EmailDropletsController.class.getName());

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
    private DropletsView dropletsView;

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

        // Supported email protocols.
        final List<String> protocols = Lists.newArrayList("imap", "imaps");

        Runnable swingTask = new Runnable() {
            @Override
            public void run() {
                setupView = new EmailDropletSetup();
                setupView.addProtocols(protocols);
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
            final String protocol = emailSettings.getProtocol();
            if (protocol.equalsIgnoreCase("imap")
                    || protocol.equalsIgnoreCase("imaps")) {
                // IMAP(S) protocol
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
                            "Could not initialize IMAP droplet", e);
                }

                droplets.put(imapsDroplet.getIdentifier(), imapsDroplet);

                saveConfig.addConfigurable(imapsDroplet);

                /*
                 * Get any emails from the newly created droplet and show to the
                 * user.
                 */
                new EmailDropletsUpdater(dropletsView, imapsDroplet).execute();

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
                new EmailDropletsUpdater(dropletsView,
                        Lists.newLinkedList(droplets.values())).execute();
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

        Icon setupIcon = null;

        try {
            setupIcon = new ImageIcon(getImage(getClass(),
                    BUNDLE.getString("email.image")));
        } catch (IOException e) {
            logger.error("Failed to load icon", e);
        }
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
