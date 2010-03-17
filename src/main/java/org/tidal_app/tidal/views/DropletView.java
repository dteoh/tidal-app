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
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.tidal_app.tidal.views.models.RippleModel;
import org.tidal_app.tidal.views.models.DropletModel;
import org.tidal_app.tidal.views.swing.DropShadowPanel;
import org.tidal_app.tidal.views.swing.GradientPanel;

/**
 * Used to visualize a single {@link DropletModel} on the interface.
 * 
 * @author Douglas Teoh
 */
public class DropletView extends DropShadowPanel {

    /**
     * Auto-generated by Eclipse.
     */
    private static final long serialVersionUID = 4663093783495843321L;

    /**
     * Models
     */
    protected transient DropletModel dropletModel;
    /**
     * Views
     */
    private transient JPanel ripplesPanel;

    private static final Font HEADER_FONT =
        new Font(Font.SANS_SERIF, Font.BOLD, 24);
    private static final Color HEADER_FOREGROUND = new Color(50, 60, 70);

    public DropletView() {
        super(6, 0.5F);
        initView();
    }

    public DropletView(final DropletModel dropletModel) {
        super(6, 0.5F);
        this.dropletModel = dropletModel;
        initView();
    }

    /**
     * Initialize the view.
     */
    private void initView() {
        assert (SwingUtilities.isEventDispatchThread());

        setLayout(new MigLayout("wrap", "[grow 100]", "[]0[]"));
        setOpaque(false);

        final JLabel dropletNameLabel =
            new JLabel(dropletModel.getDropletName().toUpperCase());
        dropletNameLabel.setForeground(HEADER_FOREGROUND);
        dropletNameLabel.setFont(HEADER_FONT);

        // Construct header panel
        final GradientPanel headerPanel =
            new GradientPanel(new Color(235, 240, 250),
                    new Color(215, 225, 235));
        headerPanel.setLayout(new MigLayout("ins 0", "[]unrel push[][]"));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createMatteBorder(1, 1, 1, 1, new Color(178, 178, 178)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        headerPanel.add(dropletNameLabel);

        // Control buttons, one for delete, another for show/hide
        headerPanel.add(new JButton(), "w 24!, h 24!");
        headerPanel.add(new JButton(), "w 24!, h 24!, wrap");

        add(headerPanel, "pushx, growx");

        final List<RippleView> rippleViews = new ArrayList<RippleView>();

        if (dropletModel != null) {
            for (final RippleModel contentModel : dropletModel
                    .getDropletContents()) {
                final RippleView rippleView = new RippleView(contentModel);
                rippleViews.add(rippleView);
            }

            Collections.sort(rippleViews);
        }

        // Construct content panel
        ripplesPanel = new JPanel();
        ripplesPanel.setLayout(new MigLayout("wrap 1, gapy 1, ins 0",
                "[grow 100]", "[]0"));
        ripplesPanel.setOpaque(false);

        for (final RippleView rippleView : rippleViews) {
            ripplesPanel.add(rippleView, "pushx, growx");
        }

        add(ripplesPanel, "pushx, growx");
    }

    public void setDropletModel(final DropletModel dropletModel) {
        assert SwingUtilities.isEventDispatchThread();

        ripplesPanel.removeAll();

        this.dropletModel = dropletModel;

        final List<RippleView> rippleViews = new ArrayList<RippleView>();

        if (dropletModel != null) {
            for (final RippleModel contentModel : dropletModel
                    .getDropletContents()) {
                final RippleView rippleView = new RippleView(contentModel);
                rippleViews.add(rippleView);
            }

            Collections.sort(rippleViews);
        }

        for (final RippleView rippleView : rippleViews) {
            ripplesPanel.add(rippleView, "pushx, growx");
        }
    }
}
