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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JLabelFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dteoh.tidal.views.MenuBarView;
import com.dteoh.tidal.views.events.MenuBarViewEvent;
import com.dteoh.tidal.views.events.MenuBarViewListener;

/**
 * Tests the MenuBarView class.
 * 
 * @author Douglas Teoh
 */
public class MenuBarViewTests {

    private FrameFixture window;
    private MenuBarView menuBarView;

    @Before
    public void setUp() {
        final JFrame testFrame = GuiActionRunner
                .execute(new GuiQuery<JFrame>() {
                    @Override
                    protected JFrame executeInEDT() throws Throwable {
                        final JFrame testFrame = new JFrame();
                        menuBarView = new MenuBarView();
                        testFrame.add(menuBarView);
                        return testFrame;
                    }
                });

        window = new FrameFixture(testFrame);
        window.show();
        window.resizeHeightTo(200);
        window.resizeWidthTo(200);
    }

    @After
    public void tearDown() {
        window.close();
        window.cleanUp();
        window = null;
        menuBarView = null;
    }

    /**
     * This test just checks if the application title label is showing.
     */
    @Test
    public void titleLabelTest() {
        final JLabelFixture titleLabel = window
                .label(new GenericTypeMatcher<JLabel>(JLabel.class) {
                    @Override
                    protected boolean isMatching(final JLabel jlabel) {
                        return "Tidal".equals(jlabel.getText());
                    }
                });

        titleLabel.requireVisible();
    }

    /**
     * Test adding a listener.
     */
    @Test
    public void testAddMenuBarViewListener() {
        MenuBarViewListener listener = mock(MenuBarViewListener.class);
        menuBarView.addMenuBarViewListener(listener);
    }

    /**
     * Test removing a listener.
     */
    @Test
    public void testRemoveMenuBarViewListener1() {
        MenuBarViewListener listener = mock(MenuBarViewListener.class);
        menuBarView.addMenuBarViewListener(listener);
        menuBarView.removeMenuBarViewListener(listener);
    }

    /**
     * Test removing non-existent listener.
     */
    @Test
    public void testRemoveMenuBarViewListener2() {
        MenuBarViewListener listener = mock(MenuBarViewListener.class);
        menuBarView.removeMenuBarViewListener(listener);
    }

    /**
     * Test if menu button click events are generated and received.
     */
    @Test
    public void testMenuButtonClicked() {
        MenuBarViewListener listener = mock(MenuBarViewListener.class);
        menuBarView.addMenuBarViewListener(listener);

        window.button("MenuBarViewMenuButton").click();

        verify(listener).menuButtonClicked(any(MenuBarViewEvent.class));
    }

}
