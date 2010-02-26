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
