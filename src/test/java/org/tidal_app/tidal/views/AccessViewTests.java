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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.swing.JFrame;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JLabelFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.tidal_app.tidal.views.events.AccessViewEvent;
import org.tidal_app.tidal.views.events.AccessViewListener;

/**
 * Tests the AccessView class.
 * 
 * @author Douglas Teoh
 */
public class AccessViewTests {

    private FrameFixture window;
    private AccessView accessView;

    @Before
    public void setUp() {
        final JFrame testFrame = GuiActionRunner
                .execute(new GuiQuery<JFrame>() {
                    @Override
                    protected JFrame executeInEDT() throws Throwable {
                        final JFrame testFrame = new JFrame();
                        accessView = new AccessView();
                        testFrame.add(accessView);
                        return testFrame;
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

    /**
     * Test text entry on the first run screen.
     */
    @Test
    public void testShowFirstRun1() {
        // Set up GUI components
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                accessView.showFirstRun();
            }
        });

        // Set up test components
        final JTextComponentFixture passwordField = window
                .textBox("AccessViewPasswordField");
        passwordField.requireText("");
        final JTextComponentFixture confirmationField = window
                .textBox("AccessViewConfirmationField");
        confirmationField.requireText("");

        final JButtonFixture unlockButton = window
                .button("AccessViewUnlockButton");
        unlockButton.requireDisabled();

        // Run the tests
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

    /**
     * Test if the password that was entered on the setup screen is the password
     * that will be returned by the generated event.
     */
    @Test
    public void testFirstRun1() {
        // Set up GUI components
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                accessView.showFirstRun();
            }
        });

        // Set up test components
        final JTextComponentFixture passwordField = window
                .textBox("AccessViewPasswordField");
        passwordField.requireText("");
        final JTextComponentFixture confirmationField = window
                .textBox("AccessViewConfirmationField");
        confirmationField.requireText("");

        final JButtonFixture unlockButton = window
                .button("AccessViewUnlockButton");
        unlockButton.requireDisabled();

        final ArgumentCaptor<AccessViewEvent> argument = ArgumentCaptor
                .forClass(AccessViewEvent.class);

        final AccessViewListener mockListener = mock(AccessViewListener.class);
        accessView.addAccessViewListener(mockListener);

        // Run the test
        final String newPassword = "_abc123.+";

        passwordField.enterText(newPassword);
        confirmationField.enterText(newPassword);
        unlockButton.click();

        verify(mockListener).setupPassword(argument.capture());
        assertEquals(newPassword, argument.getValue().getPassword());
    }

    /**
     * Test text entry on the login screen.
     */
    @Test
    public void testShowLogin1() {
        // Set up GUI components
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                accessView.showLogin();
            }
        });

        // Set up test components
        final JTextComponentFixture passwordField = window
                .textBox("AccessViewPasswordField");
        passwordField.requireText("");

        final JButtonFixture unlockButton = window
                .button("AccessViewUnlockButton");
        unlockButton.requireEnabled();

        // Run the test
        passwordField.enterText("abc 456 ___");
        passwordField.requireText("abc 456 ___");

        unlockButton.requireEnabled();
    }

    /**
     * Test if the password that is entered on the login screen is the password
     * that will be returned by the generated event.
     */
    @Test
    public void testLogin1() {
        // Set up GUI components
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                accessView.showLogin();
            }
        });

        // Set up test components
        final JTextComponentFixture passwordField = window
                .textBox("AccessViewPasswordField");
        passwordField.requireText("");
        final JButtonFixture unlockButton = window
                .button("AccessViewUnlockButton");
        unlockButton.requireEnabled();

        final ArgumentCaptor<AccessViewEvent> argument = ArgumentCaptor
                .forClass(AccessViewEvent.class);

        final AccessViewListener mockListener = mock(AccessViewListener.class);
        accessView.addAccessViewListener(mockListener);

        // Run the test
        final String password = "_abc123.+";
        passwordField.enterText(password);
        unlockButton.click();

        verify(mockListener).loginAttempted(argument.capture());
        assertEquals(password, argument.getValue().getPassword());
    }

    /**
     * Test if a custom message can be displayed on the first run screen.
     */
    @Test
    public void testDisplayMessage1() {
        // Set up GUI components
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                accessView.showFirstRun();
            }
        });

        // Set up test components
        final JLabelFixture infoLabel = window.label("AccessViewInformation");
        infoLabel.requireVisible();

        // Run the test
        final String customMessage = "Test first run message.";
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                accessView.displayMessage(customMessage);
            }
        });

        assertEquals(customMessage, infoLabel.text());
    }

    /**
     * Test if a custom message can be displayed on the login screen.
     */
    @Test
    public void testDisplayMessage2() {
        // Set up GUI components
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                accessView.showLogin();
            }
        });

        // Set up test components
        final JLabelFixture infoLabel = window.label("AccessViewInformation");
        infoLabel.requireVisible();

        // Run the test
        final String customMessage = "Test login message.";
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                accessView.displayMessage(customMessage);
            }
        });

        assertEquals(customMessage, infoLabel.text());
    }
}
