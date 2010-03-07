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

package org.tidal_app.tidal;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application entry point.
 * 
 * @author Douglas Teoh
 */
public class Tidal {

    private static final Logger LOGGER = LoggerFactory.getLogger(Tidal.class);

    // Controllers
    private final TidalController tc;
    // View
    private JFrame mainFrame;

    /**
     * Entry point into the program.
     * 
     * @param args
     */
    public static void main(final String[] args) {
        // Set the application to use system UI LnF.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            LOGGER.error("Look and feel error", e);
        } catch (InstantiationException e) {
            LOGGER.error("Look and feel error", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("Look and feel error", e);
        } catch (UnsupportedLookAndFeelException e) {
            LOGGER.error("Look and feel error", e);
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Tidal();
            }
        });
    }

    public Tidal() {
        tc = new TidalController();

        initView();

        mainFrame.setVisible(true);
    }

    /**
     * Initialize application view.
     */
    private void initView() {
        assert (SwingUtilities.isEventDispatchThread());

        // Make our application frame.
        mainFrame = new JFrame();
        mainFrame.setTitle("Tidal");
        mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                Tidal.this.exitHandler();
            }
        });

        mainFrame.setLayout(new MigLayout("ins 0", "[grow]", "[grow]"));
        mainFrame.setBackground(Color.WHITE);

        JScrollPane scrollingInterface = new JScrollPane(tc.getView());
        Border emptyBorder = new EmptyBorder(0, 0, 0, 0);
        scrollingInterface.setBorder(emptyBorder);
        scrollingInterface
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollingInterface
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        mainFrame.add(scrollingInterface, "grow 100 100");

        mainFrame.pack();

        /*
         * Since pack calculates a "big-enough" size, use it to enforce minimum
         * size.
         */
        // TODO change this.
        mainFrame.setMinimumSize(mainFrame.getSize());
    }

    private void exitHandler() {
        System.exit(0);
    }

}
