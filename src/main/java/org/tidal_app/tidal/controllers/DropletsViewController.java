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

package org.tidal_app.tidal.controllers;

import static org.tidal_app.tidal.util.EDTUtils.inEDT;

import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.tidal_app.tidal.views.DropletView;
import org.tidal_app.tidal.views.DropletsView;
import org.tidal_app.tidal.views.models.DropletModel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Used to manage the various droplets.
 * 
 * @author Douglas Teoh
 */
public class DropletsViewController implements DropletsView {

    /** View objects */
    private JPanel dropletsPanel;

    private final Map<String, DropletView> dropletViews;

    public DropletsViewController() {
        dropletViews = Maps.newHashMap();
        initView();
    }

    private void initView() {
        final Runnable swingTask = new Runnable() {
            @Override
            public void run() {
                dropletsPanel = new JPanel(new MigLayout("wrap", "[grow 100]"));
                dropletsPanel.setOpaque(false);
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            swingTask.run();
        } else {
            SwingUtilities.invokeLater(swingTask);
        }
    }

    public JComponent getView() {
        return dropletsPanel;
    }

    public void updateDropletViews(final DropletModel... dropletModels) {
        inEDT();

        final List<DropletModel> models = Lists.newLinkedList();

        updateDropletViews(models);
    }

    public void updateDropletViews(final Iterable<DropletModel> dropletModels) {
        inEDT();

        for (final DropletModel dropletModel : dropletModels) {
            DropletView dv = dropletViews.get(dropletModel.getDropletName());
            if (dv == null) {
                dv = new DropletView(dropletModel);
                dropletViews.put(dropletModel.getDropletName(), dv);
            } else {
                dv.setDropletModel(dropletModel);
            }
            dropletsPanel.add(dv, "growx, pushx");
        }
    }
}
