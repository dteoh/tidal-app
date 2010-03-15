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

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

public class DropletModel {

    private transient final String dropletName;

    private transient final List<DropletContentModel> dropletContents;

    /**
     * @return the name or identifier of this droplet.
     */
    public String getDropletName() {
        return dropletName;
    }

    public Iterable<DropletContentModel> getDropletContents() {
        return dropletContents;
    }

    public DropletModel(final String dropletName,
            final DropletContentModel... dropletContents) {
        this.dropletName = dropletName;

        final ImmutableList.Builder<DropletContentModel> modelBuilder =
            ImmutableList.builder();

        modelBuilder.add(dropletContents);

        this.dropletContents = modelBuilder.build();
    }

    public DropletModel(final String dropletName,
            final Iterator<DropletContentModel> dropletContents) {
        this.dropletName = dropletName;

        final ImmutableList.Builder<DropletContentModel> modelBuilder =
            ImmutableList.builder();

        modelBuilder.addAll(dropletContents);

        this.dropletContents = modelBuilder.build();
    }

    public DropletModel(final String dropletName,
            final Iterable<DropletContentModel> dropletContents) {
        this.dropletName = dropletName;

        final ImmutableList.Builder<DropletContentModel> modelBuilder =
            ImmutableList.builder();

        modelBuilder.addAll(dropletContents);

        this.dropletContents = modelBuilder.build();
    }
}
