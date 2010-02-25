package net.teohdouglas.tidal;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.teohdouglas.tidal.sources.EmailDroplet;
import net.teohdouglas.tidal.sources.models.EmailRipple;
import net.teohdouglas.tidal.views.models.DropletContentModel;
import net.teohdouglas.tidal.views.models.DropletModel;

public class EmailDropletsController {

    private final Map<String, EmailDroplet> droplets;

    public EmailDropletsController() {
        droplets = new TreeMap<String, EmailDroplet>();
    }

    public synchronized void addEmailDroplet(final EmailDroplet droplet) {
        droplets.put(droplet.getUsername(), droplet);
    }

    public synchronized void removeEmailDroplet(final String dropletUsername) {
        droplets.remove(dropletUsername);
    }

    public synchronized DropletModel getDropletModel(
            final String dropletUsername) {
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

    public synchronized List<DropletModel> getAllDropletModels() {
        List<DropletModel> allModels = new LinkedList<DropletModel>();
        for (String dropletUsername : droplets.keySet()) {
            EmailDroplet droplet = droplets.get(dropletUsername);
            List<DropletContentModel> contentModel =
                new LinkedList<DropletContentModel>();
            for (EmailRipple ripple : droplet.getRipples()) {
                contentModel.add(new DropletContentModel(ripple.getId(), ripple
                        .getSender(), ripple.getSubject(), ripple.getContent(),
                        ripple.getReceivedDate()));
            }
            allModels.add(new DropletModel(droplet.getUsername(), contentModel
                    .iterator()));
        }
        return allModels;
    }

}
