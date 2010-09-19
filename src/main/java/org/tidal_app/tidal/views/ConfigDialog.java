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

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.application.ResourceMap;
import org.tidal_app.tidal.views.events.ConfigDialogListener;
import org.tidal_app.tidal.views.swing.GradientPanel;

import com.dteoh.treasuremap.ResourceMaps;
import com.google.common.collect.Lists;

/**
 * Dialog for configuring existing droplet settings.
 * 
 * @author Douglas Teoh
 * 
 */
public final class ConfigDialog extends JDialog {

    /** Resource bundle for this class. */
    private static final ResourceMap BUNDLE = new ResourceMaps(
            ConfigDialog.class).build();

    /** Panel for droplet setup screen. */
    private JPanel configPanel;

    /** Panel containing command buttons. */
    private JPanel commandPanel;

    /** Listeners interested in events from the config dialog. */
    private List<ConfigDialogListener> listeners;

    /**
     * @see {@link #JDialog()}
     */
    public ConfigDialog() {
        super();
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public ConfigDialog(final Dialog owner, final boolean modal) {
        super(owner, modal);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public ConfigDialog(final Dialog owner, final String title,
            final boolean modal, final GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public ConfigDialog(final Dialog owner, final String title,
            final boolean modal) {
        super(owner, title, modal);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public ConfigDialog(final Dialog owner, final String title) {
        super(owner, title);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public ConfigDialog(final Dialog owner) {
        super(owner);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public ConfigDialog(final Frame owner, final boolean modal) {
        super(owner, modal);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public ConfigDialog(final Frame owner, final String title,
            final boolean modal, final GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public ConfigDialog(final Frame owner, final String title,
            final boolean modal) {
        super(owner, title, modal);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public ConfigDialog(final Frame owner, final String title) {
        super(owner, title);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public ConfigDialog(final Frame owner) {
        super(owner);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public ConfigDialog(final Window owner, final ModalityType modalityType) {
        super(owner, modalityType);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public ConfigDialog(final Window owner, final String title,
            final ModalityType modalityType, final GraphicsConfiguration gc) {
        super(owner, title, modalityType, gc);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public ConfigDialog(final Window owner, final String title,
            final ModalityType modalityType) {
        super(owner, title, modalityType);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public ConfigDialog(final Window owner, final String title) {
        super(owner, title);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public ConfigDialog(final Window owner) {
        super(owner);
        init();
        initView();
    }

    /**
     * Initialize non UI-related objects.
     */
    private void init() {
        listeners = Lists.newArrayList();
    }

    /**
     * Initialize UI.
     */
    @SuppressWarnings("serial")
    private void initView() {
        inEDT();

        setTitle(BUNDLE.getString("dialog.title"));

        Container container = getContentPane();
        container
                .setLayout(new MigLayout("ins 0, wrap", "[grow]", "[][grow][]"));

        // Header panel
        final GradientPanel headerPanel = new GradientPanel(
                BUNDLE.getColor("header.top.color"),
                BUNDLE.getColor("header.bottom.color"));
        headerPanel.setLayout(new MigLayout());

        JLabel heading = new JLabel();
        heading.setForeground(BUNDLE.getColor("heading.color"));
        heading.setFont(BUNDLE.getFont("heading.font"));
        heading.setName("ConfigDialogHeading");
        heading.setText(BUNDLE.getString("heading.text"));

        headerPanel.add(heading, "growx");

        container.add(headerPanel, "growx");

        // Construct the configuration panel
        configPanel = new JPanel(new MigLayout("", "[grow]", "[grow]"));
        container.add(configPanel, "grow");

        // Construct the command panel
        commandPanel = new JPanel(new MigLayout());

        final JButton cancelButton = new JButton();
        cancelButton.setName("ConfigDialogCancelButton");
        Action cancelAction = new AbstractAction(
                BUNDLE.getString("cancelAction.name")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                handleCancelButtonAction(e);
            }
        };
        cancelButton.setAction(cancelAction);

        final JButton applyButton = new JButton();
        applyButton.setName("ConfigDialogApplyButton");
        Action createAction = new AbstractAction(
                BUNDLE.getString("applyAction.name")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                handleApplyButtonAction(e);
            }
        };
        applyButton.setAction(createAction);

        final JButton deleteButton = new JButton();
        deleteButton.setName("ConfigDialogDeleteButton");
        Action deleteAction = new AbstractAction(
                BUNDLE.getString("deleteAction.name")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                handleDeleteButtonAction(e);
            }
        };
        deleteButton.setAction(deleteAction);

        // Separate this button from the others.
        commandPanel.add(deleteButton);
        // Right align the buttons
        commandPanel.add(cancelButton, "pushx, tag cancel");
        commandPanel.add(applyButton, "tag apply");

        container.add(commandPanel, "growx");
    }

    /**
     * Sets the UI component to show for the configuration dialog.
     * 
     * @param view
     */
    public void setConfigView(final JComponent view) {
        inEDT();
        configPanel.removeAll();
        configPanel.add(view, "grow");
        validate();
    }

    /**
     * Adds the specified config dialog listener to receive config dialog events
     * from this dialog.
     * 
     * @param listener
     *            Listener to add.
     */
    public void addConfigDialogListener(final ConfigDialogListener listener) {
        if (listener != null) {
            synchronized (this) {
                listeners.add(listener);
            }
        }
    }

    /**
     * Removes the specified config dialog listener so that it no longer
     * receives config dialog events from this dialog.
     * 
     * @param listener
     *            Listener to remove.
     */
    public void removeConfigDialogListener(final ConfigDialogListener listener) {
        if (listener != null) {
            synchronized (this) {
                listeners.remove(listener);
            }
        }
    }

    /**
     * Handles the action associated with the delete button.
     * 
     * @param e
     */
    private void handleDeleteButtonAction(final ActionEvent e) {
        int chosen = JOptionPane.showConfirmDialog(this,
                BUNDLE.getString("deleteDialog.message"),
                BUNDLE.getString("deleteDialog.title"),
                JOptionPane.YES_NO_OPTION);
        if (chosen == JOptionPane.YES_OPTION) {
            synchronized (this) {
                for (ConfigDialogListener l : listeners) {
                    l.delete();
                }
            }
        }
    }

    /**
     * Handles the action associated with the apply button.
     * 
     * @param e
     *            Event to handle.
     */
    private void handleApplyButtonAction(final ActionEvent e) {
        synchronized (this) {
            for (ConfigDialogListener l : listeners) {
                l.apply();
            }
        }
    }

    /**
     * Handles the action associated with the cancel button.
     * 
     * @param e
     *            Event to handle.
     */
    private void handleCancelButtonAction(final ActionEvent e) {
        synchronized (this) {
            for (ConfigDialogListener l : listeners) {
                l.cancel();
            }
        }
        setVisible(false);
    }

}
