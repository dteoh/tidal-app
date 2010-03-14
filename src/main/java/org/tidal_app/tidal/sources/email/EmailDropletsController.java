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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.SwingUtilities;

import org.tidal_app.tidal.exceptions.DropletCreationException;
import org.tidal_app.tidal.sources.email.impl.ImapDroplet;
import org.tidal_app.tidal.sources.email.models.EmailRipple;
import org.tidal_app.tidal.sources.email.models.EmailSettings;
import org.tidal_app.tidal.views.models.DropletContentModel;
import org.tidal_app.tidal.views.models.DropletModel;

public class EmailDropletsController {

    private final Map<String, EmailDroplet> droplets;

    public EmailDropletsController() {
        assert (!SwingUtilities.isEventDispatchThread());

        droplets = new TreeMap<String, EmailDroplet>();
    }

    /**
     * Given an EmailSettings object, create and add the appropriate
     * implementation of an EmailDroplet.
     * 
     * @param emailSettings
     * @throws DropletCreationException
     *             If the given email settings are incompatible with any of the
     *             EmailDroplet implementations.
     * @return the created EmailDroplet.
     */
    public synchronized EmailDroplet addEmailDroplet(
            final EmailSettings emailSettings) throws DropletCreationException {
        assert (!SwingUtilities.isEventDispatchThread());

        // Determine what type of droplet to build based on the given protocol.
        final String protocol = emailSettings.getProtocol();
        if (protocol.equalsIgnoreCase("imap")
            || protocol.equalsIgnoreCase("imaps")) {
            ImapDroplet imapsDroplet = ImapDroplet.create(emailSettings);
            droplets.put(imapsDroplet.getUsername(), imapsDroplet);
            return imapsDroplet;
        } else {
            StringBuilder sb = new StringBuilder("Unknown protocol \"");
            sb.append(protocol);
            sb.append("\"");
            throw new DropletCreationException(sb.toString());
        }
    }

    public synchronized void addEmailDroplet(final EmailDroplet droplet) {
        assert (!SwingUtilities.isEventDispatchThread());

        droplets.put(droplet.getUsername(), droplet);
    }

    public synchronized void removeEmailDroplet(final String dropletUsername) {
        assert (!SwingUtilities.isEventDispatchThread());

        EmailDroplet emailDroplet = droplets.remove(dropletUsername);
        if (emailDroplet != null) {
            emailDroplet.destroy();
        }
    }

    public synchronized DropletModel getDropletModel(
            final String dropletUsername) {
        assert (!SwingUtilities.isEventDispatchThread());

        EmailDroplet droplet = droplets.get(dropletUsername);
        if (droplet == null) {
            return null;
        }
        List<DropletContentModel> contentModel =
            new LinkedList<DropletContentModel>();
        for (EmailRipple ripple : droplet.getRipples()) {
            contentModel.add(new DropletContentModel(ripple.getId(), ripple
                    .getSender(), ripple.getSubject(), ripple.getContent(),
                    ripple.getReceivedDate()));
        }
        return new DropletModel(droplet.getUsername(), contentModel.iterator());
    }

    public synchronized Iterable<DropletModel> getAllDropletModels() {
        assert (!SwingUtilities.isEventDispatchThread());

        List<DropletModel> allModels = new LinkedList<DropletModel>();
        for (EmailDroplet droplet : droplets.values()) {
            List<DropletContentModel> contentModel =
                new LinkedList<DropletContentModel>();
            for (EmailRipple ripple : droplet.getRipples()) {
                contentModel.add(new DropletContentModel(ripple.getId(), ripple
                        .getSender(), ripple.getSubject(), ripple.getContent(),
                        ripple.getReceivedDate()));
            }
            allModels
                    .add(new DropletModel(droplet.getUsername(), contentModel));
        }
        return allModels;
    }

}
