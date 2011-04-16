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
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.application.ResourceMap;

import com.dteoh.tidal.views.models.RippleModel;
import com.dteoh.treasuremap.ResourceMaps;

/**
 * Used to visualize a ripple model.
 * 
 * @author Douglas Teoh
 */
public final class RippleView extends JPanel {

    /** Class resource bundle. */
    private static final ResourceMap BUNDLE = new ResourceMaps(RippleView.class)
            .build();

    // Style resources
    private static final Color UNSEEN_BG_COLOR = BUNDLE
            .getColor("unseen.bg.color");
    private static final Color UNSEEN_FONT_COLOR = BUNDLE
            .getColor("unseen.font.color");
    private static final Font UNSEEN_FONT_STYLE = BUNDLE.getFont("unseen.font");
    private static final Color ORIGIN_FONT_COLOR = BUNDLE
            .getColor("originLabel.foreground");
    private static final Color RECV_FONT_COLOR = BUNDLE
            .getColor("receivedLabel.foreground");
    private static final Color CONTENT_FONT_COLOR = BUNDLE
            .getColor("content.font.color");

    /** Model */
    private final RippleModel contentModel;
    /** View components */
    private JLabel originLabel;
    private JLabel subjectLabel;
    private JLabel receivedLabel;

    /**
     * Construct a new ripple view for visualizing the given model.
     * 
     * @param contentModel
     */
    public RippleView(final RippleModel contentModel) {
        super();

        this.contentModel = contentModel;
        initView();
    }

    /**
     * Test if this view has the same view model.
     */
    public boolean hasSameModel(final RippleModel model) {
        return contentModel.equals(model);
    }

    /**
     * Initialize the view.
     */
    private void initView() {
        inEDT();

        setLayout(new MigLayout("hidemode 1, wrap 3", "[][]", ""));
        setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1,
                BUNDLE.getColor("border.color")));
        setBackground(UNSEEN_BG_COLOR);

        subjectLabel = new JLabel(contentModel.getSubject());
        subjectLabel.setFont(UNSEEN_FONT_STYLE);
        subjectLabel.setForeground(UNSEEN_FONT_COLOR);
        subjectLabel.setName("RippleViewSubjectLabel");
        add(subjectLabel, "span 2, wrap");

        JTextPane previewPane = new JTextPane();
        previewPane.setEditable(false);
        String content = contentModel.getContent().trim();
        previewPane.setText(content.substring(0,
                Math.min(content.length(), 250)));
        previewPane.setBackground(UNSEEN_BG_COLOR);
        previewPane.setForeground(CONTENT_FONT_COLOR);
        add(previewPane, "span 2, growx, pushx, wrap");

        originLabel = new JLabel(contentModel.getOrigin());
        originLabel.setFont(UNSEEN_FONT_STYLE);
        originLabel.setForeground(ORIGIN_FONT_COLOR);
        originLabel.setName("RippleViewOriginLabel");
        add(originLabel);

        final SimpleDateFormat sdf = new SimpleDateFormat("MMM d, h:mm aa");
        final Calendar received = Calendar.getInstance();
        received.setTimeInMillis(contentModel.getReceived());
        receivedLabel = new JLabel(sdf.format(received.getTime()));
        receivedLabel.setFont(UNSEEN_FONT_STYLE);
        receivedLabel.setForeground(RECV_FONT_COLOR);
        receivedLabel.setName("RippleViewReceivedLabel");
        add(receivedLabel, "right");
    }
}
