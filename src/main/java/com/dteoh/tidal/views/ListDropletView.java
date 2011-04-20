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

package com.dteoh.tidal.views;

import static com.dteoh.tidal.util.EDTUtils.inEDT;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.application.ResourceMap;

import com.dteoh.tidal.util.PunchIconFactory;
import com.dteoh.tidal.views.events.DropletViewListener;
import com.dteoh.tidal.views.models.DropletModel;
import com.dteoh.tidal.views.models.RippleModel;
import com.dteoh.tidal.views.swing.AnimatedIcon;
import com.dteoh.tidal.views.swing.AnimatedLabel;
import com.dteoh.tidal.views.swing.DropShadowPanel;
import com.dteoh.tidal.views.swing.GradientPanel;
import com.dteoh.treasuremap.ResourceMaps;
import com.google.common.collect.Lists;

/**
 * Used to visualize {@link DropletModel}s on the interface. The visualization
 * of models is as a list of data feed items.
 * 
 * @author Douglas Teoh
 */
public final class ListDropletView extends DropShadowPanel implements DropletView {

    private static final ResourceMap BUNDLE = new ResourceMaps(ListDropletView.class).build();

    /** Models */
    private DropletModel dropletModel;

    /** Panel containing individual data feed items. */
    private JPanel ripplesPanel;

    /** Droplet name label. */
    private JLabel nameLabel;

    /** Droplet updating label. */
    private AnimatedLabel updatingLabel;

    /** Droplet view listeners. */
    private final List<DropletViewListener> listeners;

    /** UI settings. */
    private static final Font HEADER_FONT = BUNDLE.getFont("header.font");
    private static final Color HEADER_FOREGROUND = BUNDLE.getColor("header.foreground");

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
        super(BUNDLE.getInteger("shadow.size"), BUNDLE.getFloat("shadow.opacity"));

        listeners = Lists.newArrayList();
        initView();
    }

    /**
     * Initialize the view.
     */
    private void initView() {
        inEDT();

        setLayout(new MigLayout("wrap", "[grow 100]", "[]0[]"));
        setOpaque(false);

        // Construct header panel
        final GradientPanel headerPanel = new GradientPanel(BUNDLE.getColor("header.top.color"),
                BUNDLE.getColor("header.bottom.color"));
        headerPanel.setName("DropletViewHeaderPanel");
        headerPanel.setLayout(new MigLayout("ins 0", "[][]unrel push[]"));
        int headerBorderSize = BUNDLE.getInteger("header.border.size");
        int headerBorderPadding = BUNDLE.getInteger("header.border.padding");

        headerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(headerBorderSize,
                headerBorderSize, headerBorderSize, headerBorderSize, BUNDLE.getColor("header.border.color")),
                BorderFactory.createEmptyBorder(headerBorderPadding, headerBorderPadding, headerBorderPadding,
                        headerBorderPadding)));

        nameLabel = new JLabel();
        nameLabel.setForeground(HEADER_FOREGROUND);
        nameLabel.setFont(HEADER_FONT);
        nameLabel.setName("DropletViewNameLabel");
        headerPanel.add(nameLabel);

        Dimension frameSize = BUNDLE.getDimension("updating.frameSize");
        AnimatedIcon updatingIcon = new AnimatedIcon(BUNDLE.getImage("updating.image"), frameSize.width,
                frameSize.height);
        updatingLabel = new AnimatedLabel(updatingIcon, BUNDLE.getInteger("updating.frameInterval"));
        updatingLabel.setVisible(false);
        updatingLabel.setToolTipText(BUNDLE.getString("updating.tooltip"));
        headerPanel.add(updatingLabel, "w 22!, h 22!");

        JButton configButton = new JButton();
        configButton.setName("ListDropletViewConfigButton");
        Action configAction = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                handleConfigAction(e);
            }
        };
        configButton.setAction(configAction);
        configButton.setIcon(PunchIconFactory.createPunchedIcon(BUNDLE.getImage("settings.icon"), 2));
        configButton.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(configButton, "w 24!, h 24!, wrap");

        add(headerPanel, "pushx, growx");

        // Construct content panel
        ripplesPanel = new JPanel();
        ripplesPanel.setName("DropletViewRipplesPanel");
        ripplesPanel.setLayout(new MigLayout("wrap 1, gapy 1, ins 0", "[grow 100]", "[]0"));
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
                // I can't figure out why, but the 100px width makes the
                // rippleview's width grow and shrink properly. i.e. it is only
                // as wide as ripplesPanel instead of growing as wide as the
                // text pane that shows the email contents.
                ripplesPanel.add(new RippleView(contentModel), "growx, w 100px");
            }

            ripplesPanel.validate();
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

            for (final RippleModel contentModel : dropletModel.getDropletContents()) {
                RippleView oldView = null;
                if (ripple < oldRippleViews.length) {
                    oldView = (RippleView) oldRippleViews[ripple];
                }
                if (oldView != null && oldView.hasSameModel(contentModel)) {
                    ripplesPanel.add(oldView, "pushx, growx");
                    ripple++;
                } else {
                    ripplesPanel.add(new RippleView(contentModel), "pushx, growx");
                }
            }

            ripplesPanel.validate();
        }
    }

    @Override
    public JComponent getView() {
        return this;
    }

    @Override
    public void addDropletViewListener(final DropletViewListener listener) {
        if (listener != null) {
            synchronized (this) {
                listeners.add(listener);
            }
        }
    }

    @Override
    public void removeDropletViewListener(final DropletViewListener listener) {
        if (listener != null) {
            synchronized (this) {
                listeners.remove(listener);
            }
        }
    }

    @Override
    public void dropletUpdating(final boolean status) {
        if (status == true) {
            updatingLabel.start();
        } else {
            updatingLabel.stop();
        }

        updatingLabel.setVisible(status);
    }

    /**
     * Event handler for the config button action.
     * 
     * @param evt
     */
    private void handleConfigAction(final ActionEvent evt) {
        synchronized (this) {
            for (DropletViewListener listener : listeners) {
                listener.configAction(evt);
            }
        }
    }

}
