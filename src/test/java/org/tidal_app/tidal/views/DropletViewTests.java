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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import javax.swing.JFrame;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JLabelFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tidal_app.tidal.exceptions.EDTViolationException;
import org.tidal_app.tidal.id.ID;
import org.tidal_app.tidal.views.models.DropletModel;
import org.tidal_app.tidal.views.models.RippleModel;

/**
 * Tests the DropletView class.
 * 
 * @author Douglas Teoh
 */
public class DropletViewTests {

    private FrameFixture window;
    private DropletView view;
    private DropletModel model;

    @Before
    public void setUp() {
        final ID id = mock(ID.class);
        final String name = "ID1";
        final String origin = "test@tidal-app.org";
        final String subject = "B";
        final String content = "";
        final long received = 10000;

        model = new DropletModel(id, "Test droplet", new RippleModel(name,
                origin, subject, content, received));

        final JFrame testFrame = GuiActionRunner
                .execute(new GuiQuery<JFrame>() {
                    @Override
                    protected JFrame executeInEDT() throws Throwable {
                        final JFrame testFrame = new JFrame();
                        view = new DropletView(model);
                        testFrame.add(view);
                        return testFrame;
                    }
                });

        window = new FrameFixture(testFrame);
        window.show();
    }

    @After
    public void tearDown() {
        window.close();
        window.cleanUp();
        window = null;
        view = null;
        model = null;
    }

    /**
     * Test if the DropletView is named according to the model name.
     */
    @Test
    public void testNameLabel1() {
        final JLabelFixture nameLabel = window.label("DropletViewNameLabel");
        nameLabel.requireVisible();
        assertEquals(model.getDropletName().toUpperCase(), nameLabel.text());
    }

    /**
     * Test if the DropletView shows the ripples panel by default.
     */
    @Test
    public void testRipplesPanel1() {
        final JPanelFixture ripplesPanel = window
                .panel("DropletViewRipplesPanel");
        ripplesPanel.requireVisible();
    }

    /**
     * Test if the DropletView shows the correct number of ripples.
     */
    @Test
    public void testRipplesPanel2() {
        final JPanelFixture ripplesPanel = window
                .panel("DropletViewRipplesPanel");
        assertEquals(1, ripplesPanel.component().getComponentCount());
    }

    /**
     * Test if the view enforces its creation as being originated from the EDT.
     */
    @Test(expected = EDTViolationException.class)
    public void testInitView1() {
        new DropletView(model);
    }

    /**
     * Test if we can only set the droplet model from within the EDT.
     */
    @Test(expected = EDTViolationException.class)
    public void testSetDropletModel1() {
        view.setDropletModel(model);
    }

    /**
     * Test if setting a model with no contents will display no ripples.
     */
    @Test
    public void testSetDropletModel2() {
        final DropletModel emptyModel = new DropletModel(mock(ID.class),
                "Empty model");
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                view.setDropletModel(emptyModel);
            }
        });

        final JPanelFixture ripplesPanel = window
                .panel("DropletViewRipplesPanel");

        ripplesPanel.requireVisible();
        ripplesPanel.requireEnabled();
        assertEquals(0, ripplesPanel.component().getComponentCount());
    }

    /**
     * Test if setting a model with a different name will change the displayed
     * name of the droplet.
     */
    @Test
    public void testSetDropletModel3() {
        final String newName = "TestNewName";
        final DropletModel newModel = new DropletModel(mock(ID.class), newName);
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                view.setDropletModel(newModel);
            }
        });

        final JLabelFixture nameLabel = window.label("DropletViewNameLabel");

        assertEquals(newName.toUpperCase(), nameLabel.text());
    }

    /**
     * Test if setting a model with two content models will display the correct
     * number of ripples.
     */
    @Test
    public void testSetDropletModel4() {
        final String id1 = "ID1";
        final String origin1 = "test@tidal-app.org";
        final String subject1 = "A";
        final String content1 = "First";
        final long received1 = 10000;
        final RippleModel model1 = new RippleModel(id1, origin1, subject1,
                content1, received1);

        final String id2 = "ID2";
        final String origin2 = "test@tidal-app.org";
        final String subject2 = "B";
        final String content2 = "Second";
        final long received2 = 20000;
        final RippleModel model2 = new RippleModel(id2, origin2, subject2,
                content2, received2);

        final DropletModel newModel = new DropletModel(mock(ID.class),
                "Two models", model1, model2);

        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                view.setDropletModel(newModel);
            }
        });

        final JPanelFixture ripplesPanel = window
                .panel("DropletViewRipplesPanel");

        ripplesPanel.requireVisible();
        ripplesPanel.requireEnabled();
        assertEquals(2, ripplesPanel.component().getComponentCount());
    }

    /**
     * Test if setting a model with two content models will display sorted
     * ripples according to received time in descending order (most recent
     * first).
     */
    @Test
    public void testSetDropletModel5() {
        final String name1 = "ID1";
        final String origin1 = "test@tidal-app.org";
        final String subject1 = "A";
        final String content1 = "First";
        final long received1 = 10000;
        final RippleModel model1 = new RippleModel(name1, origin1, subject1,
                content1, received1);

        final String name2 = "ID2";
        final String origin2 = "test@tidal-app.org";
        final String subject2 = "B";
        final String content2 = "Most recent";
        final long received2 = 20000;
        final RippleModel model2 = new RippleModel(name2, origin2, subject2,
                content2, received2);

        final DropletModel newModel = new DropletModel(mock(ID.class),
                "Two models", model1, model2);

        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                view.setDropletModel(newModel);
            }
        });

        final JPanelFixture ripplesPanel = window
                .panel("DropletViewRipplesPanel");

        final RippleView rv1 = (RippleView) ripplesPanel.component()
                .getComponent(0);
        assertTrue(rv1.hasSameModel(model2));

        final RippleView rv2 = (RippleView) ripplesPanel.component()
                .getComponent(1);
        assertTrue(rv2.hasSameModel(model1));
    }

    /**
     * Test if adding a model will result in a merged display.
     */
    @Test
    public void testAddDropletModel1() {
        final String name1 = "ID1";
        final String origin1 = "test@tidal-app.org";
        final String subject1 = "A";
        final String content1 = "First";
        final long received1 = 10000;
        final RippleModel model1 = new RippleModel(name1, origin1, subject1,
                content1, received1);

        final String name2 = "ID2";
        final String origin2 = "test@tidal-app.org";
        final String subject2 = "B";
        final String content2 = "Most recent";
        final long received2 = 20000;
        final RippleModel model2 = new RippleModel(name2, origin2, subject2,
                content2, received2);

        final DropletModel newModel = new DropletModel(mock(ID.class),
                "Two models", model1, model2);

        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                view.setDropletModel(newModel);
            }
        });

        final String name3 = "ID3";
        final String origin3 = "test@tidal-app.org";
        final String subject3 = "B";
        final String content3 = "Most recent";
        final long received3 = 15000;
        final RippleModel model3 = new RippleModel(name3, origin3, subject3,
                content3, received3);

        final DropletModel newestModel = new DropletModel(mock(ID.class),
                "One model", model3);

        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                view.addDropletModel(newestModel);
            }
        });

        final JPanelFixture ripplesPanel = window
                .panel("DropletViewRipplesPanel");

        ripplesPanel.requireVisible();
        ripplesPanel.requireEnabled();
        assertEquals(3, ripplesPanel.component().getComponentCount());

    }

    /**
     * Test if adding a model will result in a merged and sorted display.
     */
    @Test
    public void testAddDropletModel2() {
        final String name1 = "ID1";
        final String origin1 = "test@tidal-app.org";
        final String subject1 = "A";
        final String content1 = "First";
        final long received1 = 10000;
        final RippleModel model1 = new RippleModel(name1, origin1, subject1,
                content1, received1);

        final String name2 = "ID2";
        final String origin2 = "test@tidal-app.org";
        final String subject2 = "B";
        final String content2 = "Most recent";
        final long received2 = 20000;
        final RippleModel model2 = new RippleModel(name2, origin2, subject2,
                content2, received2);

        final DropletModel newModel = new DropletModel(mock(ID.class),
                "Two models", model1, model2);

        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                view.setDropletModel(newModel);
            }
        });

        final String name3 = "ID3";
        final String origin3 = "test@tidal-app.org";
        final String subject3 = "B";
        final String content3 = "Most recent";
        final long received3 = 15000;
        final RippleModel model3 = new RippleModel(name3, origin3, subject3,
                content3, received3);

        final DropletModel newestModel = new DropletModel(mock(ID.class),
                "One model", model3);

        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                view.addDropletModel(newestModel);
            }
        });

        final JPanelFixture ripplesPanel = window
                .panel("DropletViewRipplesPanel");

        final RippleView rv1 = (RippleView) ripplesPanel.component()
                .getComponent(0);
        assertTrue(rv1.hasSameModel(model2));

        final RippleView rv2 = (RippleView) ripplesPanel.component()
                .getComponent(1);
        assertTrue(rv2.hasSameModel(model3));

        final RippleView rv3 = (RippleView) ripplesPanel.component()
                .getComponent(2);
        assertTrue(rv3.hasSameModel(model1));
    }
}
