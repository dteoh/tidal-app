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

package org.tidal_app.tidal.views;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.tidal_app.tidal.views.models.DropletModel;

/**
 * Used to visualize a number of {@link DropletModel} objects on the interface.
 * 
 * @author Douglas Teoh
 */
public class DropletsView extends JPanel {

    /**
     * Auto generated by Eclipse
     */
    private static final long serialVersionUID = -2013255389551067933L;
    private JPanel dropletsPanel;
    private JPanel dropletsContentPanel;
    private final MouseAdapter dropletViewMouseAdapter;

    public DropletsView() {
        super();
        dropletViewMouseAdapter = new DropletViewMouseAdapter();
        initView();
    }

    /**
     * Initialize the view.
     */
    private void initView() {
        assert (SwingUtilities.isEventDispatchThread());

        setLayout(new MigLayout("", "[200]0[600::, grow 100]", ""));
        setBackground(Color.WHITE);

        dropletsPanel = new JPanel();
        dropletsPanel.setLayout(new MigLayout("wrap 1, ins 0"));
        dropletsPanel.setBackground(Color.WHITE);

        dropletsContentPanel = new JPanel();
        dropletsContentPanel.setLayout(new MigLayout("wrap 1, ins 0, gapy 0"));
        dropletsContentPanel.setBorder(BorderFactory.createLineBorder(
                new Color(102, 148, 227), 4));
        dropletsContentPanel.setBackground(Color.WHITE);

        add(dropletsPanel, "grow 100 100");
        add(dropletsContentPanel, "grow 100 100");
    }

    /**
     * Displays the given droplet models on the interface.
     * 
     * @param dropletModels
     */
    public void displayDroplets(final DropletModel... dropletModels) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                dropletsPanel.removeAll();
                dropletsContentPanel.removeAll();

                PoolDropletView poolView = new PoolDropletView();
                poolView.addMouseListener(dropletViewMouseAdapter);
                dropletsPanel.add(poolView);

                for (int i = 0; i < dropletModels.length; i++) {
                    DropletView dropletView = new DropletView(dropletModels[i]);
                    dropletView.addMouseListener(dropletViewMouseAdapter);
                    poolView.addRippleViews(dropletView.getRippleViews());
                    dropletsPanel.add(dropletView);
                }
            }
        });
        dropletsPanel.revalidate();
    }

    /**
     * Displays the given droplet models on the interface.
     * 
     * @param dropletModels
     */
    public void displayDroplets(final Iterator<DropletModel> dropletModels) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                dropletsPanel.removeAll();
                dropletsContentPanel.removeAll();

                while (dropletModels.hasNext()) {
                    DropletView dropletView =
                        new DropletView(dropletModels.next());
                    dropletView.addMouseListener(dropletViewMouseAdapter);
                    dropletsPanel.add(dropletView);
                }
            }
        });
        dropletsPanel.revalidate();
    }

    private class DropletViewMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(final MouseEvent e) {
            if (e.isPopupTrigger()) {
                return;
            }

            for (Component c : dropletsPanel.getComponents()) {
                DropletView view = (DropletView) c;
                view.deselect();
            }

            dropletsContentPanel.removeAll();

            DropletView view = (DropletView) e.getSource();
            view.select();

            for (RippleView rippleView : view.getRippleViews()) {
                dropletsContentPanel.add(rippleView, "pushx, growx");
            }

            dropletsContentPanel.revalidate();
        }
    }
}
