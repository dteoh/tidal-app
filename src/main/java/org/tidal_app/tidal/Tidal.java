package org.tidal_app.tidal;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class.
 * 
 * @author douglas
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
        new Tidal();
    }

    public Tidal() {
        tc = new TidalController();

        initView();

        mainFrame.setVisible(true);
    }

    private void initView() {
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

        // Make a menu bar
        // JMenuBar menuBar = new JMenuBar();
        // JMenu tidalMenu = new JMenu("Tidal");
        // menuBar.add(tidalMenu);

        // mainFrame.setJMenuBar(menuBar);

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
        mainFrame.setMinimumSize(mainFrame.getSize());
    }

    private void exitHandler() {
        System.exit(0);
    }

}
