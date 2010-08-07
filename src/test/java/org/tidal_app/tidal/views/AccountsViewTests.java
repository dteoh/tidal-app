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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.DialogFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tidal_app.tidal.sources.SetupDroplet;
import org.tidal_app.tidal.util.ResourceUtils;

/**
 * Tests for the accounts setup view.
 * 
 * @author Douglas Teoh
 * 
 */
public class AccountsViewTests {

    /** Mock setup handler 1. */
    private SetupDroplet mockSD1;
    private final String mockViewName1 = "TestSetupView1";
    private ImageIcon mockSDIcon1;

    /** Mock setup handler 2. */
    private SetupDroplet mockSD2;
    private final String mockViewName2 = "TestSetupView2";
    private ImageIcon mockSDIcon2;

    /** The accounts setup window. */
    private AccountsView av;
    private DialogFixture dialog;

    @Before
    public void setUp() {
        try {
            mockSDIcon1 = new ImageIcon(ResourceUtils.getImage(getClass(),
                    "one.png"));
        } catch (IOException e1) {
            mockSDIcon1 = null;
        }
        mockSD1 = mock(SetupDroplet.class);
        when(mockSD1.getSetupIcon()).thenReturn(mockSDIcon1);
        when(mockSD1.getSetupView()).thenReturn(new JPanel() {
            {
                setName(mockViewName1);
            }
        });

        try {
            mockSDIcon2 = new ImageIcon(ResourceUtils.getImage(getClass(),
                    "two.png"));
        } catch (IOException e1) {
            mockSDIcon2 = null;
        }
        mockSD2 = mock(SetupDroplet.class);
        when(mockSD2.getSetupIcon()).thenReturn(mockSDIcon2);
        when(mockSD2.getSetupView()).thenReturn(new JPanel() {
            {
                setName(mockViewName2);
            }
        });

        av = GuiActionRunner.execute(new GuiQuery<AccountsView>() {
            @Override
            protected AccountsView executeInEDT() throws Throwable {
                return new AccountsView();
            }
        });

        dialog = new DialogFixture(av);
        dialog.show();
        dialog.resizeTo(new Dimension(500, 500));
    }

    @After
    public void tearDown() {
        if (dialog.component().isVisible()) {
            dialog.close();
        }
        dialog.cleanUp();
        dialog = null;
        av = null;
        mockSD1 = null;
    }

    /**
     * Test if adding a SetupDroplet handler will have it's own view shown on
     * the interface.
     */
    @Test
    public void testAddSetupView1() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                av.addSetupView(mockSD1);
            }
        });

        verify(mockSD1).getSetupIcon();
        verify(mockSD1).getSetupView();

        // Check if button is set with icon and showing.
        dialog.button(new GenericTypeMatcher<JButton>(JButton.class) {
            @Override
            protected boolean isMatching(final JButton button) {
                return button.getIcon() != null
                        && button.getIcon().equals(mockSDIcon1);
            }
        }).requireEnabled().requireVisible();

        // Check if setup panel is showing
        dialog.panel(mockViewName1).requireEnabled().requireVisible();
    }

    /**
     * Test if adding two SetupDroplet handlers will have the first handler's
     * view displayed and both handler's buttons displayed.
     */
    @Test
    public void testAddSetupView2() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                av.addSetupView(mockSD1);
                av.addSetupView(mockSD2);
            }
        });

        verify(mockSD1).getSetupIcon();
        verify(mockSD1).getSetupView();

        // The second view will not be retrieved until we ask for it.
        verify(mockSD2).getSetupIcon();

        // Check if the buttons are set with their icons and showing.
        dialog.button(new GenericTypeMatcher<JButton>(JButton.class) {
            @Override
            protected boolean isMatching(final JButton button) {
                return button.getIcon() != null
                        && button.getIcon().equals(mockSDIcon1);
            }
        }).requireEnabled().requireVisible();

        dialog.button(new GenericTypeMatcher<JButton>(JButton.class) {
            @Override
            protected boolean isMatching(final JButton button) {
                return button.getIcon() != null
                        && button.getIcon().equals(mockSDIcon2);
            }
        }).requireEnabled().requireVisible();

        // Check if setup panel is showing
        dialog.panel(mockViewName1).requireEnabled().requireVisible();
    }

    /**
     * Test showing the setup view using the button.
     */
    @Test
    public void testShowSetup1() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                av.addSetupView(mockSD1);
            }
        });

        // Check if setup panel is showing
        dialog.panel(mockViewName1).requireEnabled().requireVisible();

        // Check if the buttons are set with their icons, showing, and click on
        // it.
        dialog.button(new GenericTypeMatcher<JButton>(JButton.class) {
            @Override
            protected boolean isMatching(final JButton button) {
                return button.getIcon() != null
                        && button.getIcon().equals(mockSDIcon1);
            }
        }).requireEnabled().requireVisible().click();

        // Check if setup panel is still showing
        dialog.panel(mockViewName1).requireEnabled().requireVisible();
    }

    /**
     * Test showing the second setup view using the button.
     */
    @Test
    public void testShowSetup2() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                av.addSetupView(mockSD1);
                av.addSetupView(mockSD2);
            }
        });

        // Check if setup panel is showing
        dialog.panel(mockViewName1).requireEnabled().requireVisible();

        // Show the second setup view.
        dialog.button(new GenericTypeMatcher<JButton>(JButton.class) {
            @Override
            protected boolean isMatching(final JButton button) {
                return button.getIcon() != null
                        && button.getIcon().equals(mockSDIcon2);
            }
        }).requireEnabled().requireVisible().click();

        // Check if the second setup view is shown.
        dialog.panel(mockViewName2).requireEnabled().requireVisible();
    }

    /**
     * Test cancelling setup.
     */
    @Test
    public void testCancelSetup1() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                av.addSetupView(mockSD1);
            }
        });

        dialog.button("AccountsViewCancelButton").requireEnabled()
                .requireVisible().click();

        verify(mockSD1).cancelSetup();

        dialog.requireNotVisible();
    }

    /**
     * Test cancelling setup.
     */
    @Test
    public void testCancelSetup2() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                av.addSetupView(mockSD1);
                av.addSetupView(mockSD2);
            }
        });

        dialog.button("AccountsViewCancelButton").requireEnabled()
                .requireVisible().click();

        verify(mockSD1).cancelSetup();
        verify(mockSD2).cancelSetup();

        dialog.requireNotVisible();
    }

    /**
     * Test successful setup.
     */
    @Test
    public void testCreate1() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                av.addSetupView(mockSD1);
            }
        });

        when(mockSD1.createDropletFromSetup()).thenReturn(true);

        // Click setup
        dialog.button("AccountsViewCreateButton").requireEnabled()
                .requireVisible().click();

        // Check that we got the message to create a droplet
        verify(mockSD1).createDropletFromSetup();

        // Check that the dialog is now closed
        dialog.requireNotVisible();
    }

    /**
     * Test failed setup.
     */
    @Test
    public void testCreate2() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                av.addSetupView(mockSD1);
            }
        });

        when(mockSD1.createDropletFromSetup()).thenReturn(false);

        dialog.button("AccountsViewCreateButton").requireEnabled()
                .requireVisible().click();

        verify(mockSD1).createDropletFromSetup();

        // On a failed setup, the dialog should still be visible
        dialog.requireVisible();
        // ... and there should be an error message.
        dialog.optionPane().requireErrorMessage().requireVisible();
    }

    /**
     * Test successful setup on the second setup handler.
     */
    @Test
    public void testCreate3() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                av.addSetupView(mockSD1);
                av.addSetupView(mockSD2);
            }
        });

        when(mockSD2.createDropletFromSetup()).thenReturn(true);
        verify(mockSD1).getSetupIcon();
        verify(mockSD1).getSetupView();

        // Show the second setup view.
        dialog.button(new GenericTypeMatcher<JButton>(JButton.class) {
            @Override
            protected boolean isMatching(final JButton button) {
                return button.getIcon() != null
                        && button.getIcon().equals(mockSDIcon2);
            }
        }).requireEnabled().requireVisible().click();

        // Click setup
        dialog.button("AccountsViewCreateButton").requireEnabled()
                .requireVisible().click();

        // Check that we got the message to create a droplet
        verify(mockSD2).createDropletFromSetup();

        // Check that the first handler did not get any message to create a
        // droplet.
        verify(mockSD1).cancelSetup();

        // Check that the dialog is now closed
        dialog.requireNotVisible();
    }

    /**
     * Test failed setup on the second handler.
     */
    @Test
    public void testCreate4() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                av.addSetupView(mockSD1);
                av.addSetupView(mockSD2);
            }
        });

        when(mockSD2.createDropletFromSetup()).thenReturn(false);

        // Show the second setup view.
        dialog.button(new GenericTypeMatcher<JButton>(JButton.class) {
            @Override
            protected boolean isMatching(final JButton button) {
                return button.getIcon() != null
                        && button.getIcon().equals(mockSDIcon2);
            }
        }).requireEnabled().requireVisible().click();

        // Click setup
        dialog.button("AccountsViewCreateButton").requireEnabled()
                .requireVisible().click();

        verify(mockSD2).createDropletFromSetup();

        // On a failed setup, the dialog should still be visible
        dialog.requireVisible();
        // ... and there should be an error message.
        dialog.optionPane().requireErrorMessage().requireVisible();
    }
}
