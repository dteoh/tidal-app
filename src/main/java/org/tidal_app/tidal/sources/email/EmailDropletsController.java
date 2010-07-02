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

import java.util.List;
import java.util.Map;

import org.tidal_app.tidal.exceptions.DropletCreationException;
import org.tidal_app.tidal.sources.email.impl.ImapDroplet;
import org.tidal_app.tidal.sources.email.models.EmailRipple;
import org.tidal_app.tidal.sources.email.models.EmailSettings;
import org.tidal_app.tidal.views.models.DropletModel;
import org.tidal_app.tidal.views.models.RippleModel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class EmailDropletsController {

    private final Map<String, AbstractEmailDroplet> droplets;

    public EmailDropletsController() {
        outsideEDT();

        droplets = Maps.newTreeMap();
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

                droplets.put(imapsDroplet.getUsername(), imapsDroplet);
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

}
