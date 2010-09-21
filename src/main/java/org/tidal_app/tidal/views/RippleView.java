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
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.application.ResourceMap;
import org.tidal_app.tidal.views.models.RippleModel;

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

    // Colors for the various message states
    private static final Color SEEN_BG_COLOR = BUNDLE.getColor("seen.bg.color");
    private static final Color UNSEEN_BG_COLOR = BUNDLE
            .getColor("unseen.bg.color");
    private static final Color SEEN_FONT_COLOR = BUNDLE
            .getColor("seen.font.color");
    private static final Color READING_FONT_COLOR = BUNDLE
            .getColor("reading.font.color");
    private static final Color PREVIEW_FONT_COLOR = BUNDLE
            .getColor("preview.font.color");

    // Font styles for the various message states
    private static final Font UNSEEN_FONT_STYLE = BUNDLE.getFont("unseen.font");
    private static final Font SEEN_FONT_STYLE = BUNDLE.getFont("seen.font");
    private static final Font READING_SUBJECT_FONT_STYLE = BUNDLE
            .getFont("reading.subject.font");
    private static final Font READING_DATE_FONT_STYLE = BUNDLE
            .getFont("reading.date.font");

    /** Model */
    private final RippleModel contentModel;
    /** View components */
    private JLabel originLabel;
    private JLabel subjectLabel;
    private JLabel previewLabel;
    private JLabel receivedLabel;
    private JEditorPane contents;

    public RippleView(final RippleModel contentModel) {
        super();

        this.contentModel = contentModel;
        initView();
        addMouseListener(new RippleViewMouseAdapter());
    }

    public boolean hasSameModel(final RippleModel model) {
        return contentModel.equals(model);
    }

    /**
     * Initialize the view.
     */
    private void initView() {
        inEDT();

        setLayout(new MigLayout("hidemode 1, wrap 3", "[][grow 100][]", ""));
        setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1,
                BUNDLE.getColor("border.color")));
        setBackground(UNSEEN_BG_COLOR);

        originLabel = new JLabel(contentModel.getOrigin());
        originLabel.setFont(UNSEEN_FONT_STYLE);
        originLabel.setName("RippleViewOriginLabel");
        add(originLabel, "w ::15%");

        subjectLabel = new JLabel(contentModel.getSubject());
        subjectLabel.setFont(UNSEEN_FONT_STYLE);
        subjectLabel.setName("RippleViewSubjectLabel");
        add(subjectLabel, "split 2, left");

        final int previewLength = contentModel.getContent().length() > 100 ? 100
                : contentModel.getContent().length();
        String previewString = contentModel.getContent().substring(0,
                previewLength);
        if (!previewString.isEmpty()) {
            previewString = " - ".concat(previewString);
        }
        if (contentModel.getContent().length() > 100) {
            previewString = previewString.concat(" ...");
        }

        previewLabel = new JLabel(previewString);
        previewLabel.setForeground(PREVIEW_FONT_COLOR);
        previewLabel.setFont(SEEN_FONT_STYLE);
        previewLabel.setName("RippleViewPreviewLabel");
        add(previewLabel, "left");

        final SimpleDateFormat sdf = new SimpleDateFormat("MMM d, h:mm aa");
        final Calendar received = Calendar.getInstance();
        received.setTimeInMillis(contentModel.getReceived());
        receivedLabel = new JLabel(sdf.format(received.getTime()));
        receivedLabel.setFont(UNSEEN_FONT_STYLE);
        receivedLabel.setName("RippleViewReceivedLabel");
        add(receivedLabel, "right, w ::10%");

        contents = new JEditorPane();
        contents.setName("RippleViewContents");
        contents.setEditable(false);
        contents.setVisible(false);
        contents.setOpaque(false);

        // Don't bother with HTML for now; this is just too slow + it seems
        // that external content are being downloaded from the EDT, thus
        // causing GUI freeze.
        contents.setContentType("text/plain");

        // Running into problems with large amounts of content slowing rendering
        // down; implemented temporary fix of limiting content length.
        contents.setText(contentModel.getContent().substring(0,
                Math.min(contentModel.getContent().length(), 2048)));
        contents.setFont(SEEN_FONT_STYLE);
        int contentsPadding = BUNDLE.getInteger("contents.padding");
        contents.setBorder(BorderFactory.createEmptyBorder(contentsPadding,
                contentsPadding, contentsPadding, contentsPadding));

        // Disabled; not needed for plain text documents.
        // Need to add custom CSS rule or the editor pane will use a serif font
        // with a small font size.
        // final StringBuilder bodyRule = new StringBuilder();
        // bodyRule.append("body { font-family: ");
        // bodyRule.append(SEEN_FONT_STYLE.getFamily());
        // bodyRule.append("; font-size: ");
        // bodyRule.append(SEEN_FONT_STYLE.getSize());
        // bodyRule.append("pt; }");
        // ((HTMLDocument) contents.getDocument()).getStyleSheet().addRule(
        // bodyRule.toString());
        add(contents, "skip, growx, pushx");
    }

    /**
     * Hides or shows the message, depending on previous state.
     */
    private void showHideMessage() {
        inEDT();

        if (contents.isVisible()) {
            // Hide the contents of the message
            contents.setVisible(false);

            setBackground(SEEN_BG_COLOR);

            originLabel.setFont(SEEN_FONT_STYLE);
            subjectLabel.setFont(SEEN_FONT_STYLE);
            receivedLabel.setFont(SEEN_FONT_STYLE);

            receivedLabel.setForeground(SEEN_FONT_COLOR);
            originLabel.setForeground(SEEN_FONT_COLOR);
            subjectLabel.setForeground(SEEN_FONT_COLOR);
            previewLabel.setVisible(true);
        } else {
            // Show the contents of the message
            contents.setVisible(true);
            setBackground(UNSEEN_BG_COLOR);

            subjectLabel.setFont(READING_SUBJECT_FONT_STYLE);
            receivedLabel.setFont(READING_DATE_FONT_STYLE);

            receivedLabel.setForeground(READING_FONT_COLOR);
            originLabel.setForeground(READING_FONT_COLOR);
            subjectLabel.setForeground(READING_FONT_COLOR);
            previewLabel.setVisible(false);
        }
    }

    /**
     * Helper method for wrapping strings into HTML content.
     * 
     * @param content
     * @return Original string wrapped in div markup.
     */
    private String wrapHTML(final String content) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<div width=\"500px\">");
        sb.append(content);
        sb.append("</div>");
        return sb.toString();
    }

    /**
     * Inner class for handling mouse events.
     * 
     * @author Douglas Teoh
     */
    private class RippleViewMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(final MouseEvent e) {
            showHideMessage();
        }
    }

}
