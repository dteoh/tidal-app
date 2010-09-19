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

package org.tidal_app.tidal.sources.email;

import static org.tidal_app.tidal.util.EDTUtils.inEDT;
import static org.tidal_app.tidal.util.ResourceUtils.getDimension;

import java.awt.Dialog.ModalityType;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.WindowConstants;

import org.jdesktop.application.ResourceMap;
import org.tidal_app.tidal.sources.email.models.Protocol;
import org.tidal_app.tidal.sources.email.views.EmailDropletSetup;
import org.tidal_app.tidal.views.ConfigDialog;
import org.tidal_app.tidal.views.events.ConfigDialogListener;

import com.dteoh.treasuremap.ResourceMaps;

/**
 * Helper class for configuration dialogs.
 * 
 * @author Douglas Teoh
 * 
 */
final class EmailDropletConfig {

    /** Class resource bundle. */
    private static final ResourceMap BUNDLE = new ResourceMaps(
            EmailDropletConfig.class).build();

    /** Configuration dialog. */
    private final ConfigDialog confDialog;
    /** Setup view. */
    private final EmailDropletSetup confView;

    /**
     * Should be invoked from the EDT.
     */
    EmailDropletConfig() {
        inEDT();

        confView = new EmailDropletSetup();
        confView.addProtocols(Protocol.values());

        confDialog = new ConfigDialog();
        confDialog.setModalityType(ModalityType.APPLICATION_MODAL);
        confDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        confDialog.setMinimumSize(getDimension(BUNDLE
                .getString("confDialog.minSize")));
        confDialog.setLocationRelativeTo(null);
        confDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                confView.clearFields();
            }
        });
        confDialog.setConfigView(confView);
    }

    /**
     * @see ConfigDialog#addConfigDialogListener(ConfigDialogListener)
     */
    void addConfigDialogListener(final ConfigDialogListener listener) {
        confDialog.addConfigDialogListener(listener);
    }

    /**
     * @see ConfigDialog#removeConfigDialogListener(ConfigDialogListener)
     */
    void removeConfigDialogListener(final ConfigDialogListener listener) {
        confDialog.removeConfigDialogListener(listener);
    }

    /**
     * Retrieve the configuration associated with the dialog.
     */
    EmailDropletSetup getConfigurationView() {
        return confView;
    }

    /**
     * Show the configuration dialog. The dialog is centered on the screen and
     * is modal.
     */
    void show() {
        confDialog.setVisible(true);
    }

    /**
     * Hide the configuration dialog.
     */
    void hide() {
        confDialog.setVisible(false);
        confView.clearFields();
    }

}
