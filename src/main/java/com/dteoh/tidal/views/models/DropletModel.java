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

package com.dteoh.tidal.views.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import com.dteoh.tidal.id.ID;
import com.dteoh.tidal.views.DropletView;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

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

        ArrayList<RippleModel> models = Lists.newArrayList(first
                .getDropletContents());
        models.addAll(second.dropletContents);
        Collections.sort(models);

        ArrayList<RippleModel> uniques = Lists.newArrayList();

        RippleModel prev = null;
        for (RippleModel model : models) {
            if (prev != null && model.equals(prev)) {
                continue;
            }
            uniques.add(model);
            prev = model;
        }

        final ImmutableList.Builder<RippleModel> modelBuilder = ImmutableList
                .builder();
        modelBuilder.addAll(uniques);

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
