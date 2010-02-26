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

    private final String dropletName;

    private final List<DropletContentModel> dropletContents;

    public String getDropletName() {
        return dropletName;
    }

    public Iterator<DropletContentModel> getDropletContents() {
        return dropletContents.iterator();
    }

    public DropletModel(final String dropletName,
            final DropletContentModel... dropletContents) {
        this.dropletName = dropletName;

        ImmutableList.Builder<DropletContentModel> contentModelBuilder =
            ImmutableList.builder();

        contentModelBuilder.add(dropletContents);

        this.dropletContents = contentModelBuilder.build();
    }

    public DropletModel(final String dropletName,
            final Iterator<DropletContentModel> dropletContents) {
        this.dropletName = dropletName;

        ImmutableList.Builder<DropletContentModel> contentModelBuilder =
            ImmutableList.builder();

        contentModelBuilder.addAll(dropletContents);

        this.dropletContents = contentModelBuilder.build();
    }
}
