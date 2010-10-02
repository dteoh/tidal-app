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

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JLabelFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.junit.After;
import org.junit.Test;
import org.tidal_app.tidal.exceptions.EDTViolationException;
import org.tidal_app.tidal.views.models.RippleModel;

/**
 * Tests the RippleView class.
 * 
 * @author Douglas Teoh
 */
public class RippleViewTests {

    private FrameFixture window;
    private JPanelFixture mainPanel;
    private JLabelFixture originLabel;
    private JLabelFixture subjectLabel;
    private JLabelFixture previewLabel;
    private JLabelFixture receivedLabel;
    private RippleView rippleView;

    /**
     * Sets up the test view.
     * 
     * @param model
     * @throws Exception
     */
    public void setUp(final RippleModel model) throws Exception {
        final JFrame testFrame = GuiActionRunner
                .execute(new GuiQuery<JFrame>() {
                    @Override
                    protected JFrame executeInEDT() throws Throwable {
                        final JFrame testFrame = new JFrame();
                        rippleView = new RippleView(model);
                        testFrame.add(rippleView);
                        return testFrame;
                    }
                });

        window = new FrameFixture(testFrame);
        window.show();
        window.resizeHeightTo(200);

        mainPanel = window.panel(new GenericTypeMatcher<JPanel>(JPanel.class) {
            @Override
            protected boolean isMatching(final JPanel panel) {
                return RippleView.class.equals(panel.getClass());
            }
        });

        originLabel = window.label("RippleViewOriginLabel");
        subjectLabel = window.label("RippleViewSubjectLabel");
        previewLabel = window.label("RippleViewPreviewLabel");
        receivedLabel = window.label("RippleViewReceivedLabel");
    }

    @After
    public void tearDown() throws Exception {
        originLabel = null;
        subjectLabel = null;
        previewLabel = null;
        receivedLabel = null;
        mainPanel = null;
        if (window != null) {
            window.close();
            window.cleanUp();
            window = null;
        }
        rippleView = null;
    }

    /**
     * Test if the view enforces its creation as being originated from the EDT.
     */
    @Test(expected = EDTViolationException.class)
    public void testInitView1() {
        final String id1 = "ID1";
        final String origin1 = "test@tidal-app.org";
        final String subject1 = "Test subject";
        final String content1 = "Lorem ipsum dolor sit amet, consectetur "
                + "adipiscing elit. In"
                + " elementum tortor vitae felis sollicitudin sed suscipit"
                + " ligula molestie. Vestibulum tincidunt tincidunt mi, "
                + "et pretium ligula venenatis amet. ";
        final long received1 = 10000;

        final RippleModel model1 = new RippleModel(id1, origin1, subject1,
                content1, received1);

        new RippleView(model1);
    }

}
