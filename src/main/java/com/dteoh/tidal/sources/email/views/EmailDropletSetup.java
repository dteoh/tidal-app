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

package com.dteoh.tidal.sources.email.views;

import static com.dteoh.tidal.util.EDTUtils.inEDT;

import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.application.ResourceMap;

import com.dteoh.tidal.sources.email.models.EmailSettings;
import com.dteoh.tidal.sources.email.models.Protocol;
import com.dteoh.treasuremap.ResourceMaps;

/**
 * Setup view for email droplets.
 * 
 * @author Douglas Teoh
 * 
 */
public final class EmailDropletSetup extends JPanel {

    /** Resource bundle for this class. */
    private static final ResourceMap BUNDLE = new ResourceMaps(
            EmailDropletSetup.class).build();

    public static void main(final String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception e) {
        }

        Runnable edtTask = new Runnable() {

            @Override
            public void run() {

                JFrame frame = new JFrame();
                frame.setLayout(new MigLayout("", "[grow]", "[grow]"));
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

                frame.add(new EmailDropletSetup(), "grow");

                frame.setVisible(true);
            }
        };

        SwingUtilities.invokeLater(edtTask);

    }

    /** Server value field. */
    private JTextField serverField;

    /** Protocol value drop-down menu. */
    private JComboBox protocolField;

    /** Username value field. */
    private JTextField usernameField;

    /** Password value field. */
    private JPasswordField passwordField;

    public EmailDropletSetup() {
        super();
        initView();
    }

    /**
     * Initialize UI.
     */
    private void initView() {
        inEDT();

        setLayout(new MigLayout("wrap 2", "[right]15[left]", "[]10"));

        setBorder(BorderFactory.createTitledBorder(BUNDLE
                .getString("titleBorder.text")));

        // Server
        JLabel serverLabel = new JLabel();
        serverLabel.setText(BUNDLE.getString("serverLabel.text"));
        serverLabel.setName("EmailDropletSetupServerLabel");

        serverField = new JTextField();

        add(serverLabel);
        add(serverField, "w 200::");

        // Protocol
        JLabel protocolLabel = new JLabel();
        protocolLabel.setText(BUNDLE.getString("protocolLabel.text"));
        protocolLabel.setName("EmailDropletSetupProtocolLabel");

        protocolField = new JComboBox();

        add(protocolLabel);
        add(protocolField, "w 200::");

        // Username
        JLabel usernameLabel = new JLabel();
        usernameLabel.setText(BUNDLE.getString("usernameLabel.text"));
        usernameLabel.setName("EmailDropletSetupUsernameLabel");

        usernameField = new JTextField();

        add(usernameLabel);
        add(usernameField, "w 200::");

        // Password
        JLabel passwordLabel = new JLabel();
        passwordLabel.setText(BUNDLE.getString("passwordLabel.text"));
        passwordLabel.setName("EmailDropletSetupPasswordLabel");

        passwordField = new JPasswordField();

        add(passwordLabel);
        add(passwordField, "w 200::");
    }

    /**
     * Add protocol strings to the protocol combo box.
     * 
     * @param protocols
     *            collection of protocols.
     */
    public void addProtocols(final Protocol... protocols) {
        inEDT();

        for (Protocol p : protocols) {
            protocolField.addItem(p);
        }
    }

    /**
     * Clear input fields.
     */
    public void clearFields() {
        inEDT();

        serverField.setText("");
        usernameField.setText("");
        char[] password = passwordField.getPassword();
        Arrays.fill(password, '\0');
        passwordField.setText("");
    }

    /**
     * Get the field values as a model.
     * 
     * @return model with the setup field values.
     */
    public EmailSettings getSettings() {
        inEDT();

        String password = new String(passwordField.getPassword());
        EmailSettings settings = new EmailSettings(serverField.getText(),
                (Protocol) protocolField.getSelectedItem(),
                usernameField.getText(), password);
        return settings;
    }

    /**
     * Set the field values using the given settings model.
     * 
     * @param settings
     */
    public void setSettings(final EmailSettings settings) {
        inEDT();

        serverField.setText(settings.getHost());
        protocolField.setSelectedItem(settings.getProtocol());
        usernameField.setText(settings.getUsername());
        passwordField.setText(settings.getPassword());
    }

}
