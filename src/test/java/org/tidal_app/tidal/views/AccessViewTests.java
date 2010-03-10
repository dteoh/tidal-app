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

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Douglas Teoh
 */
public class AccessViewTests {

    private FrameFixture window;
    private AccessView accessView;

    @Before
    public void setUp() {
        JFrame testFrame = GuiActionRunner.execute(new GuiQuery<JFrame>() {
            @Override
            protected JFrame executeInEDT() throws Throwable {
                return new JFrame() {
                    {
                        accessView = new AccessView();
                        add(accessView);
                    }
                };
            }
        });

        window = new FrameFixture(testFrame);
        window.show();
        window.resizeHeightTo(400);
        window.resizeWidthTo(400);
    }

    @After
    public void tearDown() {
        window.close();
        window.cleanUp();
        window = null;
        accessView = null;
    }

    @Test
    public void testShowFirstRun1() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                accessView.showFirstRun();
            }
        });

        JTextComponentFixture passwordField =
            window.textBox("AccessViewPasswordField");
        passwordField.requireText("");
        JTextComponentFixture confirmationField =
            window.textBox("AccessViewConfirmationField");
        confirmationField.requireText("");

        JButtonFixture unlockButton = window.button("AccessViewUnlockButton");
        unlockButton.requireDisabled();

        passwordField.enterText("a");
        unlockButton.requireDisabled();
        confirmationField.enterText("a");
        unlockButton.requireEnabled();

        passwordField.enterText("abc");
        unlockButton.requireDisabled();
        confirmationField.enterText("abc");
        unlockButton.requireEnabled();

        passwordField.enterText("abc123");
        unlockButton.requireDisabled();
        confirmationField.enterText("abc123");
        unlockButton.requireEnabled();

        passwordField.enterText("a_b_c");
        unlockButton.requireDisabled();
        confirmationField.enterText("a_b_c");
        unlockButton.requireEnabled();

        passwordField.enterText("4 b_c");
        unlockButton.requireDisabled();
        confirmationField.enterText("4 b_c");
        unlockButton.requireEnabled();
    }

    @Test
    public void testShowLogin1() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                accessView.showLogin();
            }
        });

        JTextComponentFixture passwordField =
            window.textBox("AccessViewPasswordField");
        passwordField.requireText("");

        JButtonFixture unlockButton = window.button("AccessViewUnlockButton");
        unlockButton.requireEnabled();

        passwordField.enterText("abc 456 ___");
        passwordField.requireText("abc 456 ___");

        unlockButton.requireEnabled();
    }
}
