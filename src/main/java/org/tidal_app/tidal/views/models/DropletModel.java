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

package org.tidal_app.tidal.views.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.tidal_app.tidal.id.ID;
import org.tidal_app.tidal.views.DropletView;

import com.google.common.collect.ImmutableList;

/**
 * View model for {@link DropletView}.
 * 
 * @author Douglas Teoh
 * 
 */
public class DropletModel {

    /** Droplet identifier. */
    private final ID dropletID;
    /** Name of the model. */
    private final String dropletName;
    /** Droplet contents. */
    private final List<RippleModel> dropletContents;

    /**
     * @return the identifier of this droplet.
     */
    public ID getIdentifier() {
        return dropletID;
    }

    /**
     * @return the name of this droplet.
     */
    public String getDropletName() {
        return dropletName;
    }

    /**
     * @return droplet contents.
     */
    public Iterable<RippleModel> getDropletContents() {
        return dropletContents;
    }

    /**
     * Creates a new droplet view model.
     * 
     * @param dropletName
     *            Name of the droplet.
     * @param dropletContents
     *            Contents of the view model.
     */
    public DropletModel(final ID identifier, final String dropletName,
            final RippleModel... dropletContents) {
        dropletID = identifier;
        this.dropletName = dropletName;

        final List<RippleModel> models = new ArrayList<RippleModel>();
        for (final RippleModel model : dropletContents) {
            models.add(model);
        }
        Collections.sort(models);

        final ImmutableList.Builder<RippleModel> modelBuilder = ImmutableList
                .builder();
        modelBuilder.addAll(models);

        this.dropletContents = modelBuilder.build();
    }

    /**
     * Creates a new droplet view model.
     * 
     * @param dropletName
     *            Name of the droplet.
     * @param dropletContents
     *            Contents of the view model.
     */
    public DropletModel(final ID identifier, final String dropletName,
            final Iterable<RippleModel> dropletContents) {
        dropletID = identifier;
        this.dropletName = dropletName;

        final List<RippleModel> models = new ArrayList<RippleModel>();
        for (final RippleModel model : dropletContents) {
            models.add(model);
        }
        Collections.sort(models);

        final ImmutableList.Builder<RippleModel> modelBuilder = ImmutableList
                .builder();
        modelBuilder.addAll(models);

        this.dropletContents = modelBuilder.build();
    }

    /**
     * Creates a new droplet view model by merging two droplet view models
     * together. Duplicated contents will be removed.
     * 
     * @param first
     *            Base model to use when merging.
     * @param second
     *            Merge this model into the first model.
     */
    private DropletModel(final DropletModel first, final DropletModel second) {
        dropletID = first.getIdentifier();
        dropletName = first.getDropletName();

        final List<RippleModel> models = new ArrayList<RippleModel>();

        final Iterator<RippleModel> firstIt = first.getDropletContents()
                .iterator();
        final Iterator<RippleModel> secondIt = second.getDropletContents()
                .iterator();

        // Merge the sorted ripple models together; duplicates will be removed.
        while (firstIt.hasNext() || secondIt.hasNext()) {
            RippleModel firstModel = null;
            RippleModel secondModel = null;
            if (firstIt.hasNext()) {
                firstModel = firstIt.next();
            }
            if (secondIt.hasNext()) {
                secondModel = secondIt.next();
            }
            if (firstModel != null && secondModel != null) {
                if (firstModel.compareTo(secondModel) <= 0) {
                    /*
                     * Add only unique models. compareTo does not have same
                     * semantics as equals.
                     */
                    if (!firstModel.equals(secondModel)) {
                        models.add(firstModel);
                        models.add(secondModel);
                    } else {
                        models.add(firstModel);
                    }
                } else {
                    /*
                     * Add only unique models. compareTo does not have same
                     * semantics as equals.
                     */
                    if (!firstModel.equals(secondModel)) {
                        models.add(secondModel);
                        models.add(firstModel);
                    } else {
                        models.add(firstModel);
                    }
                }
            } else if (firstModel != null) {
                models.add(firstModel);
            } else if (secondModel != null) {
                models.add(secondModel);
            }
        }

        final ImmutableList.Builder<RippleModel> modelBuilder = ImmutableList
                .builder();
        modelBuilder.addAll(models);

        dropletContents = modelBuilder.build();
    }

    /**
     * Merge a droplet model with {@code this} model.
     * 
     * @param other
     *            model to merge with.
     * @return a new droplet model merged with the given model. Does not return
     *         a new model if other is null.
     */
    public DropletModel mergeWith(final DropletModel other) {
        if (other != null) {
            return new DropletModel(this, other);
        }
        return this;
    }
}
