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

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.application.ResourceMap;

import com.dteoh.tidal.sources.SetupDroplet;
import com.dteoh.tidal.views.swing.GradientPanel;
import com.dteoh.treasuremap.ResourceMaps;
import com.google.common.collect.Maps;

import foxtrot.Job;
import foxtrot.Worker;

/**
 * Dialog for setting up new accounts.
 * 
 * @author Douglas Teoh
 * 
 */
public final class AccountsDialog extends JDialog {

    /** Resource bundle for this class. */
    private static final ResourceMap BUNDLE = new ResourceMaps(
            AccountsDialog.class).build();

    /** Panel for the various droplet icons. */
    private JPanel iconPanel;

    /** Panel for droplet setup screen. */
    private JPanel setupPanel;

    /** Panel containing command buttons. */
    private JPanel commandPanel;

    /**
     * Mapping between show setup buttons and their associated droplet
     * controller.
     */
    private Map<JButton, SetupDroplet> buttonMap;

    /** The active droplet being configured. */
    private SetupDroplet activeDroplet;

    /**
     * @see {@link #JDialog()}
     */
    public AccountsDialog() {
        super();
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public AccountsDialog(final Dialog owner, final boolean modal) {
        super(owner, modal);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public AccountsDialog(final Dialog owner, final String title,
            final boolean modal, final GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public AccountsDialog(final Dialog owner, final String title,
            final boolean modal) {
        super(owner, title, modal);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public AccountsDialog(final Dialog owner, final String title) {
        super(owner, title);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public AccountsDialog(final Dialog owner) {
        super(owner);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public AccountsDialog(final Frame owner, final boolean modal) {
        super(owner, modal);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public AccountsDialog(final Frame owner, final String title,
            final boolean modal, final GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public AccountsDialog(final Frame owner, final String title,
            final boolean modal) {
        super(owner, title, modal);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public AccountsDialog(final Frame owner, final String title) {
        super(owner, title);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public AccountsDialog(final Frame owner) {
        super(owner);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public AccountsDialog(final Window owner, final ModalityType modalityType) {
        super(owner, modalityType);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public AccountsDialog(final Window owner, final String title,
            final ModalityType modalityType, final GraphicsConfiguration gc) {
        super(owner, title, modalityType, gc);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public AccountsDialog(final Window owner, final String title,
            final ModalityType modalityType) {
        super(owner, title, modalityType);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public AccountsDialog(final Window owner, final String title) {
        super(owner, title);
        init();
        initView();
    }

    /**
     * @see {@link #JDialog()}
     */
    public AccountsDialog(final Window owner) {
        super(owner);
        init();
        initView();
    }

    /**
     * Initialize non UI-related objects.
     */
    private void init() {
        buttonMap = Maps.newHashMap();
    }

    /**
     * Initialize UI.
     */
    @SuppressWarnings("serial")
    private void initView() {
        inEDT();

        setTitle(BUNDLE.getString("dialog.title"));

        Container container = getContentPane();
        container.setLayout(new MigLayout("ins 0, wrap", "[grow]",
                "[][][grow][]"));

        // Header panel
        final GradientPanel headerPanel = new GradientPanel(
                BUNDLE.getColor("header.top.color"),
                BUNDLE.getColor("header.bottom.color"));
        headerPanel.setLayout(new MigLayout());

        JLabel heading = new JLabel();
        heading.setForeground(BUNDLE.getColor("heading.color"));
        heading.setFont(BUNDLE.getFont("heading.font"));
        heading.setName("AccountsViewHeading");
        heading.setText(BUNDLE.getString("heading.text"));

        headerPanel.add(heading, "growx");

        container.add(headerPanel, "growx");

        // Construct our icon panel
        iconPanel = new GradientPanel(BUNDLE.getColor("iconPanel.top.color"),
                BUNDLE.getColor("iconPanel.bottom.color"));
        iconPanel.setLayout(new MigLayout());

        container.add(iconPanel, "growx");

        // Construct the setup panel
        setupPanel = new JPanel(new MigLayout("", "[grow]", "[grow]"));
        container.add(setupPanel, "grow");

        // Construct the command panel
        commandPanel = new JPanel(new MigLayout());

        JButton cancelButton = new JButton();
        cancelButton.setName("AccountsViewCancelButton");
        Action cancelAction = new AbstractAction(
                BUNDLE.getString("cancelAction.name")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                handleCancelButtonAction(e);
            }
        };
        cancelButton.setAction(cancelAction);

        final JButton createButton = new JButton();
        createButton.setName("AccountsViewCreateButton");
        Action createAction = new AbstractAction(
                BUNDLE.getString("createAction.name")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                createButton.setText(BUNDLE
                        .getString("createButton.text.pending"));
                createButton.setEnabled(false);

                handleCreateButtonAction(e);

                createButton.setText(BUNDLE.getString("createAction.name"));
                createButton.setEnabled(true);
            }
        };
        createButton.setAction(createAction);

        // Right align the buttons
        commandPanel.add(cancelButton, "pushx, tag cancel");
        commandPanel.add(createButton, "tag apply");

        container.add(commandPanel, "growx");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                cancelSetups();
            }
        });
    }

    /**
     * Helper function for cancelling setup across all views.
     */
    private void cancelSetups() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (SetupDroplet droplet : buttonMap.values()) {
                    droplet.cancelSetup();
                }
            }
        });
    }

    /**
     * Handles the action associated with the cancel button.
     * 
     * @param e
     *            Event to handle.
     */
    private void handleCancelButtonAction(final ActionEvent e) {
        cancelSetups();
        setVisible(false);
    }

    /**
     * Handles the action associated with the create button.
     * 
     * @param e
     *            Event to handle.
     */
    private void handleCreateButtonAction(final ActionEvent e) {

        if (activeDroplet != null) {
            boolean created = activeDroplet.createDropletFromSetup();
            if (created) {
                cancelSetups();
                setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this,
                        BUNDLE.getString("create.error.message"),
                        BUNDLE.getString("create.error.title"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handles the show setup button action.
     * 
     * @param e
     *            Event to handle.
     */
    private void handleShowSetupAction(final ActionEvent e) {
        /*
         * Get the droplet associated with the setup button and show its setup
         * screen.
         */
        SetupDroplet droplet = buttonMap.get(e.getSource());
        activeDroplet = droplet;
        setupPanel.removeAll();
        setupPanel.add(droplet.getSetupView(), "grow");
        validate();
    }

    /**
     * Add the given droplet controller to the accounts setup view.
     * 
     * @param droplet
     *            Droplet controller to add.
     */
    public void addSetupView(final SetupDroplet droplet) {
        inEDT();

        Icon icon = (Icon) Worker.post(new Job() {
            @Override
            public Object run() {
                return droplet.getSetupIcon();
            }
        });

        JButton showSetupButton = new JButton();
        showSetupButton.setIcon(icon);
        showSetupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                handleShowSetupAction(e);
            }
        });

        buttonMap.put(showSetupButton, droplet);

        iconPanel.add(showSetupButton, "w 80!, h 80!");
        iconPanel.validate();

        // If there is no active setup screen, show this one.
        if (activeDroplet == null) {
            activeDroplet = droplet;
            setupPanel.add(droplet.getSetupView(), "grow");
            validate();
        }
    }

    /**
     * Helper for creating the progress dialog. Not currently in use as the EDT
     * seems to die.
     */
    private JDialog createProgressDialog() {
        JDialog progDialog = new JDialog(this);

        progDialog.setTitle(BUNDLE.getString("progress.title"));
        progDialog.setModalityType(ModalityType.APPLICATION_MODAL);
        progDialog.setLocationRelativeTo(this);
        progDialog
                .setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        progDialog.setSize(BUNDLE.getDimension("progress.size"));
        progDialog.setResizable(false);

        Container progContainer = progDialog.getContentPane();
        progContainer.setLayout(new MigLayout());

        // Progress bar.
        JProgressBar progBar = new JProgressBar(SwingConstants.HORIZONTAL);
        progBar.setIndeterminate(true);
        progContainer.add(progBar, "growx, pushx, wrap");

        // Progress message.
        JLabel progLabel = new JLabel(BUNDLE.getString("progress.text"));
        progLabel.setHorizontalAlignment(SwingConstants.CENTER);
        progContainer.add(progLabel, "growx, pushx");

        return progDialog;
    }
}
