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

import java.util.List;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tidal_app.tidal.sources.email.models.EmailRipple;
import org.tidal_app.tidal.views.DropletsView;
import org.tidal_app.tidal.views.models.DropletModel;
import org.tidal_app.tidal.views.models.RippleModel;

import com.google.common.collect.Lists;

/**
 * Used to check for emails and display the results on the user interface.
 * 
 * @author Douglas Teoh
 * 
 */
public class EmailDropletsUpdater extends SwingWorker<Void, DropletModel> {

    /** Updater logger. */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(EmailDropletsUpdater.class);

    /** Droplets to retrieve emails from. */
    private final Iterable<AbstractEmailDroplet> droplets;

    /** View to display updates on. */
    private final DropletsView view;

    /**
     * Creates a new background updater.
     * 
     * @param view
     *            The view to display updates on.
     * @param droplets
     *            The droplets to retrieve emails from.
     */
    public EmailDropletsUpdater(final DropletsView view,
            final Iterable<AbstractEmailDroplet> droplets) {
        this.droplets = droplets;
        this.view = view;
    }

    /**
     * Creates a new background updater.
     * 
     * @param view
     *            The view to display updates on.
     * @param droplets
     *            The droplets to retrieve emails from.
     */
    public EmailDropletsUpdater(final DropletsView view,
            final AbstractEmailDroplet... droplets) {
        this.view = view;
        this.droplets = Lists.newArrayList(droplets);
    }

    @Override
    protected Void doInBackground() throws Exception {
        for (AbstractEmailDroplet droplet : droplets) {
            publish(getDropletModel(droplet));
        }

        return null;
    }

    /**
     * Display results on the UI.
     * 
     * @see javax.swing.SwingWorker#process(java.util.List)
     */
    @Override
    protected void process(final List<DropletModel> chunks) {
        view.updateDropletViews(chunks);
        LOGGER.debug("Updated droplet views");
    }

    /**
     * Must call get() incase of exceptions thrown from {@link #doInBackground}.
     * 
     * @see javax.swing.SwingWorker#done()
     */
    @Override
    protected void done() {
        try {
            get();
        } catch (Exception e) {
            LOGGER.error("Email droplets update error", e);
        }
    }

    /**
     * Helper function for getting a droplet model from a droplet.
     * 
     * @param droplet
     *            Droplet to use.
     * @return Retrieved droplet model.
     */
    private DropletModel getDropletModel(final AbstractEmailDroplet droplet) {
        LOGGER.debug("Retrieving emails");

        final List<RippleModel> contentModel = Lists.newLinkedList();

        for (final EmailRipple ripple : droplet.getRipples()) {
            contentModel.add(new RippleModel(ripple.getId(),
                    ripple.getSender(), ripple.getSubject(), ripple
                            .getContent(), ripple.getEpochSentTime()));
            LOGGER.debug("ID: {}, Subject: '{}'", ripple.getId(),
                    ripple.getSubject());
        }
        return new DropletModel(droplet.getUsername(), contentModel);
    }

}
