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

import static org.tidal_app.tidal.util.EDTUtils.inEDT;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.tidal_app.tidal.views.models.DropletModel;
import org.tidal_app.tidal.views.models.RippleModel;
import org.tidal_app.tidal.views.swing.DropShadowPanel;
import org.tidal_app.tidal.views.swing.GradientPanel;

/**
 * Used to visualize {@link DropletModel}s on the interface. The visualization
 * of models is as a list of data feed items.
 * 
 * @author Douglas Teoh
 */
public final class ListDropletView extends DropShadowPanel implements
        DropletView {

    /** Models */
    private DropletModel dropletModel;

    /** Panel containing individual data feed items. */
    private JPanel ripplesPanel;

    /** Droplet name label. */
    private JLabel nameLabel;

    // TODO refactor out into resource map.
    private static final Font HEADER_FONT = new Font(Font.SANS_SERIF,
            Font.BOLD, 24);
    private static final Color HEADER_FOREGROUND = new Color(50, 60, 70);

    /**
     * Creates a new ListDropletView with no model.
     * 
     * @return The created view.
     */
    public static ListDropletView create() {
        return new ListDropletView();
    }

    /**
     * Creates a new ListDropletView with no model.
     */
    private ListDropletView() {
        // TODO refactor magic constants.
        super(6, 0.5F);
        initView();
    }

    /**
     * Initialize the view.
     */
    private void initView() {
        inEDT();

        setLayout(new MigLayout("wrap", "[grow 100]", "[]0[]"));
        setOpaque(false);

        nameLabel = new JLabel();
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
    }

    @Override
    public void setDropletModel(final DropletModel model) {
        inEDT();

        if (model != null) {
            nameLabel.setText(model.getDropletName().toUpperCase());

            ripplesPanel.removeAll();
            dropletModel = model;

            for (final RippleModel contentModel : model.getDropletContents()) {
                ripplesPanel.add(new RippleView(contentModel), "pushx, growx");
            }
        }
    }

    @Override
    public void addDropletModel(final DropletModel model) {
        inEDT();

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

    @Override
    public JComponent getView() {
        return this;
    }
}
