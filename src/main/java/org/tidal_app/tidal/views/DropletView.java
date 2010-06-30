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
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.tidal_app.tidal.views.models.DropletModel;
import org.tidal_app.tidal.views.models.RippleModel;
import org.tidal_app.tidal.views.swing.DropShadowPanel;
import org.tidal_app.tidal.views.swing.GradientPanel;

/**
 * Used to visualize a single {@link DropletModel} on the interface.
 * 
 * @author Douglas Teoh
 */
public class DropletView extends DropShadowPanel {

    /**
     * Models
     */
    protected DropletModel dropletModel;
    /**
     * Views
     */
    private JPanel ripplesPanel;
    private JLabel nameLabel;

    private static final Font HEADER_FONT = new Font(Font.SANS_SERIF,
            Font.BOLD, 24);
    private static final Color HEADER_FOREGROUND = new Color(50, 60, 70);

    public DropletView(final DropletModel dropletModel) {
        super(6, 0.5F);
        this.dropletModel = dropletModel;
        initView();
    }

    /**
     * Initialize the view.
     */
    private void initView() {
        assert SwingUtilities.isEventDispatchThread();

        setLayout(new MigLayout("wrap", "[grow 100]", "[]0[]"));
        setOpaque(false);

        nameLabel = new JLabel(dropletModel.getDropletName().toUpperCase());
        nameLabel.setForeground(HEADER_FOREGROUND);
        nameLabel.setFont(HEADER_FONT);
        nameLabel.setName("DropletViewNameLabel");

        // Construct header panel
        final GradientPanel headerPanel = new GradientPanel(new Color(235, 240,
                250), new Color(215, 225, 235));
        headerPanel.setName("DropletViewHeaderPanel");
        headerPanel.setLayout(new MigLayout("ins 0", "[]unrel push[][]"));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createMatteBorder(1, 1, 1, 1, new Color(178, 178, 178)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        headerPanel.add(nameLabel);

        // Control buttons, one for delete, another for show/hide
        headerPanel.add(new JButton(), "w 24!, h 24!");
        headerPanel.add(new JButton(), "w 24!, h 24!, wrap");

        add(headerPanel, "pushx, growx");

        // Construct content panel
        ripplesPanel = new JPanel();
        ripplesPanel.setName("DropletViewRipplesPanel");
        ripplesPanel.setLayout(new MigLayout("wrap 1, gapy 1, ins 0",
                "[grow 100]", "[]0"));
        ripplesPanel.setOpaque(false);

        add(ripplesPanel, "pushx, growx");

        if (dropletModel != null) {
            for (final RippleModel contentModel : dropletModel
                    .getDropletContents()) {
                ripplesPanel.add(new RippleView(contentModel), "pushx, growx");
            }
        }
    }

    public void setDropletModel(final DropletModel model) {
        assert SwingUtilities.isEventDispatchThread();

        if (model != null) {
            nameLabel.setText(model.getDropletName().toUpperCase());

            ripplesPanel.removeAll();
            dropletModel = model;

            for (final RippleModel contentModel : model.getDropletContents()) {
                ripplesPanel.add(new RippleView(contentModel), "pushx, growx");
            }
        }
    }

    public void addDropletModel(final DropletModel model) {
        assert SwingUtilities.isEventDispatchThread();

        final Component[] oldRippleViews = ripplesPanel.getComponents();
        int ripple = 0;

        if (oldRippleViews.length == 0) {
            setDropletModel(model);
            return;
        }

        if (model != null) {
            ripplesPanel.removeAll();
            dropletModel = dropletModel.mergeWith(model);

            for (final RippleModel contentModel : dropletModel
                    .getDropletContents()) {
                RippleView oldView = null;
                if (ripple < oldRippleViews.length) {
                    oldView = (RippleView) oldRippleViews[ripple];
                }
                if (oldView != null && oldView.hasSameModel(contentModel)) {
                    ripplesPanel.add(oldView, "pushx, growx");
                    ripple++;
                } else {
                    ripplesPanel.add(new RippleView(contentModel),
                            "pushx, growx");
                }
            }
        }

    }
}
