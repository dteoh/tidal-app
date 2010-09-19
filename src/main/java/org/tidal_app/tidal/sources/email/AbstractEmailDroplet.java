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

import static org.tidal_app.tidal.util.EDTUtils.inEDT;

import java.awt.event.ActionEvent;

import org.slf4j.Logger;
import org.tidal_app.tidal.exceptions.DropletInitException;
import org.tidal_app.tidal.id.ID;
import org.tidal_app.tidal.sources.Droplet;
import org.tidal_app.tidal.sources.email.models.EmailSettings;
import org.tidal_app.tidal.sources.email.models.Protocol;
import org.tidal_app.tidal.util.EDTUtils;
import org.tidal_app.tidal.views.DropletView;
import org.tidal_app.tidal.views.DropletViews;
import org.tidal_app.tidal.views.events.ConfigDialogListener;
import org.tidal_app.tidal.views.events.DropletViewListener;
import org.tidal_app.tidal.views.models.DropletModel;
import org.tidal_app.tidal.views.models.RippleModel;

import foxtrot.Job;
import foxtrot.Task;
import foxtrot.Worker;

/**
 * Generic email droplet.
 * 
 * @author Douglas Teoh
 */
public abstract class AbstractEmailDroplet implements Droplet {

    /** Droplet identifier. */
    protected final ID identifier;

    /** Email settings. */
    protected EmailSettings settings;

    /** View associated with the droplet. */
    protected DropletView view;

    /** Listener to droplet view events. */
    protected DropletViewListener dvl = new DropletViewListener() {
        @Override
        public void configAction(final ActionEvent evt) {
            configHandler.getConfigurationView().setSettings(getSettings());
            configHandler.show();
        }
    };

    /** Handles the configuration view. */
    protected EmailDropletConfig configHandler;

    /** Listener to configuration dialog events. */
    protected ConfigDialogListener cdl = new ConfigDialogListener() {
        @Override
        public void delete() {
            // TODO Auto-generated method stub

        }

        @Override
        public void cancel() {
            configHandler.hide();
        }

        @Override
        public void apply() {
            EmailSettings newSettings = configHandler.getConfigurationView()
                    .getSettings();
            handleReconfig(newSettings);
        }
    };

    protected AbstractEmailDroplet(final ID identifier,
            final EmailSettings settings) {
        this.settings = settings;
        this.identifier = identifier;
        initView();
    }

    protected AbstractEmailDroplet(final ID identifier, final String host,
            final Protocol protocol, final String username,
            final String password) {
        settings = new EmailSettings(host, protocol, username, password);
        this.identifier = identifier;
        initView();
    }

    /**
     * Initialize the droplet view.
     */
    protected void initView() {
        EDTUtils.runOnEDT(new Runnable() {
            @Override
            public void run() {
                view = DropletViews.newListView();
                view.addDropletViewListener(dvl);

                configHandler = new EmailDropletConfig();
                configHandler.addConfigDialogListener(cdl);
            }
        });
    }

    /**
     * Returns the username associated with this droplet.
     */
    public String getUsername() {
        return settings.getUsername();
    }

    /**
     * Returns the settings associated with this droplet.
     */
    public EmailSettings getSettings() {
        return settings;
    }

    /**
     * Returns the identifier associated with this droplet.
     */
    public ID getIdentifier() {
        return identifier;
    }

    @Override
    public abstract void init() throws DropletInitException;

    @Override
    public abstract void update();

    @Override
    public void destroy() {
        EDTUtils.runOnEDT(new Runnable() {
            @Override
            public void run() {
                view.removeDropletViewListener(dvl);
                configHandler.removeConfigDialogListener(cdl);
            }
        });
    }

    @Override
    public DropletView getDropletView() {
        inEDT();
        return view;
    }

    /**
     * Retrieves updated information feed items.
     */
    public abstract Iterable<RippleModel> getRipples();

    /**
     * Get logger instance.
     */
    protected abstract Logger getLogger();

    /**
     * Restart the droplet.
     */
    protected abstract void restart() throws DropletInitException;

    /**
     * Reconfigure the current droplet using the given settings.
     * 
     * @param settings
     *            The new settings.
     */
    protected void handleReconfig(final EmailSettings settings) {
        if (settings == null) {
            throw new NullPointerException();
        }

        EmailSettings oldSettings = this.settings;
        this.settings = settings;
        try {
            Worker.post(new Task() {
                @Override
                public Object run() throws Exception {
                    restart();
                    return null;
                }
            });
        } catch (Exception e) {
            getLogger().error("Droplet reconfiguration error", e);

            // Restore the old settings so that we have something that works.
            handleReconfig(oldSettings);
            return;
        }

        // Success, lets get fresh emails.
        @SuppressWarnings("unchecked")
        Iterable<RippleModel> rms = (Iterable<RippleModel>) Worker
                .post(new Job() {
                    @Override
                    public Object run() {
                        return getRipples();
                    }
                });

        final DropletModel dm = new DropletModel(getIdentifier(),
                getUsername(), rms);

        EDTUtils.runOnEDT(new Runnable() {
            @Override
            public void run() {
                view.setDropletModel(dm);
                configHandler.hide();
            }
        });
    }

}
