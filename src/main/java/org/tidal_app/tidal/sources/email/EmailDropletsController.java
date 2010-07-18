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

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.tidal_app.tidal.configuration.SaveConfigurable;
import org.tidal_app.tidal.exceptions.DropletCreationException;
import org.tidal_app.tidal.exceptions.DropletInitException;
import org.tidal_app.tidal.guice.InjectLogger;
import org.tidal_app.tidal.sources.SetupDroplet;
import org.tidal_app.tidal.sources.email.impl.ImapDroplet;
import org.tidal_app.tidal.sources.email.models.EmailRipple;
import org.tidal_app.tidal.sources.email.models.EmailSettings;
import org.tidal_app.tidal.sources.email.views.EmailDropletSetup;
import org.tidal_app.tidal.views.DropletsView;
import org.tidal_app.tidal.views.models.DropletModel;
import org.tidal_app.tidal.views.models.RippleModel;

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

    @InjectLogger
    private Logger logger;

    /** Resource bundle for this class. */
    private static final ResourceBundle BUNDLE = ResourceBundle
            .getBundle(EmailDropletsController.class.getName());

    private final Map<String, AbstractEmailDroplet> droplets;

    private EmailDropletSetup setupView;

    @Inject
    private SaveConfigurable saveConfig;

    @Inject
    private DropletsView dropletsView;

    public EmailDropletsController() {
        droplets = Maps.newTreeMap();

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
                if (droplets.containsKey(emailSettings.getUsername())) {
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

                droplets.put(imapsDroplet.getUsername(), imapsDroplet);

                saveConfig.addConfigurable(imapsDroplet);

                return imapsDroplet;
            } else {
                // Unknown/unsupported protocols.
                final StringBuilder sb = new StringBuilder(
                        "Unknown protocol \"");
                sb.append(protocol);
                sb.append("\"");
                throw new DropletCreationException(sb.toString());
            }
        }
    }

    /**
     * TODO: Review for removal.
     * 
     * @param droplet
     * @throws DropletCreationException
     */
    public void addEmailDroplet(final AbstractEmailDroplet droplet)
            throws DropletCreationException {
        outsideEDT();

        synchronized (this) {
            // Disallow overwriting existing mappings.
            if (droplets.containsKey(droplet.getUsername())) {
                throw new DropletCreationException(
                        "Duplicate AbstractEmailDroplet for "
                                + droplet.getUsername());
            }

            // Initialize the droplet
            try {
                droplet.init();
            } catch (DropletInitException e) {
                throw new DropletCreationException(
                        "Could not initialize email droplet", e);
            }

            droplets.put(droplet.getUsername(), droplet);
        }

    }

    /**
     * Removes and destroys a droplet being managed by the controller.
     * 
     * @param dropletUsername
     *            username identifying the droplet to be destroyed.
     * @return true if the droplet exists and is destroyed, false otherwise.
     */
    public boolean destroyEmailDroplet(final String dropletUsername) {
        outsideEDT();

        synchronized (this) {
            final AbstractEmailDroplet abstractEmailDroplet = droplets
                    .remove(dropletUsername);
            if (abstractEmailDroplet != null) {
                abstractEmailDroplet.destroy();
                return true;
            }
        }
        return false;
    }

    /**
     * TODO: Replace with callback mechanism.
     * 
     * @param dropletUsername
     * @return
     */
    public DropletModel getDropletModel(final String dropletUsername) {
        outsideEDT();

        synchronized (this) {
            final AbstractEmailDroplet droplet = droplets.get(dropletUsername);

            if (droplet == null) {
                return null;
            }

            final List<RippleModel> contentModel = Lists.newLinkedList();

            for (final EmailRipple ripple : droplet.getRipples()) {
                contentModel.add(new RippleModel(ripple.getId(), ripple
                        .getSender(), ripple.getSubject(), ripple.getContent(),
                        ripple.getEpochSentTime()));
            }
            return new DropletModel(droplet.getUsername(), contentModel);
        }
    }

    /**
     * TODO: replace with callback mechanism.
     * 
     * @return
     */
    public Iterable<DropletModel> getAllDropletModels() {
        outsideEDT();

        synchronized (this) {
            final List<DropletModel> allModels = Lists.newLinkedList();

            for (final AbstractEmailDroplet droplet : droplets.values()) {
                final List<RippleModel> contentModel = Lists.newLinkedList();

                for (final EmailRipple ripple : droplet.getRipples()) {
                    contentModel.add(new RippleModel(ripple.getId(), ripple
                            .getSender(), ripple.getSubject(), ripple
                            .getContent(), ripple.getEpochSentTime()));
                }
                allModels.add(new DropletModel(droplet.getUsername(),
                        contentModel));
            }
            return allModels;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tidal_app.tidal.sources.SetupDroplet#getSetupView()
     */
    @Override
    public JComponent getSetupView() {
        return setupView;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tidal_app.tidal.sources.SetupDroplet#getSetupIcon()
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see org.tidal_app.tidal.sources.SetupDroplet#cancelSetup()
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see org.tidal_app.tidal.sources.SetupDroplet#createDropletFromSetup()
     */
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

}
