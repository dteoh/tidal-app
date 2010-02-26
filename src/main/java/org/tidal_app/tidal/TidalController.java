package org.tidal_app.tidal;

import java.util.Calendar;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.tidal_app.tidal.views.DropletsView;
import org.tidal_app.tidal.views.models.DropletContentModel;
import org.tidal_app.tidal.views.models.DropletModel;


/**
 * Main application controller. This controller is responsible for adding new
 * accounts to the application and scheduling email checking. This controller is
 * also responsible for coordinating other controllers.
 * 
 * @author douglas
 */
public class TidalController {

    /** The plate controller's view */
    private DropletsView view;
    private JTextField accountsField;
    private final EmailDropletsController ac;

    public TidalController() {
        ac = new EmailDropletsController();
        initView();
    }

    public JComponent getView() {
        return view;
    }

    private void initView() {
        view = new DropletsView();

        // TESTING
        DropletModel m =
            new DropletModel(
                    "Test",
                    new DropletContentModel(
                            1,
                            "Douglas Teoh",
                            "Just a test email",
                            "Lorem ipsum dolor sit amet, consectetur "
                                + "adipiscing elit. Etiam felis leo, elementum "
                                + "interdum interdum at, mollis sed nibh. Mauris "
                                + "laoreet metus sed massa sagittis tempor. "
                                + "Pellentesque diam est, adipiscing non vehicula id,"
                                + " aliquet eget ligula. Aenea scelerisque dictum"
                                + " nulla eget semper. Sed dapibus accumsan ligula "
                                + "eget faucibus. Donec vitae interdum neque. Aliquam"
                                + " sed erat vitae dui lacinia tincidunt a eu velit."
                                + " Suspendisse in nulla sit amet urna semper "
                                + "tincidunt pharetra at neque. Pellentesque et "
                                + "turpis eget urna aliquet elementum at sed massa. "
                                + "Fusce facilisis lorem id mi lobortis in rhoncus "
                                + "urna viverra. Praesent pulvinar volutpat urna."
                                + "\nCras molestie sollicitudin ultrices. In quis est"
                                + " sit amet dolor egestas interdum. Vestibulum "
                                + "consectetur enim ultricies nisl tristique varius. "
                                + "Etiam et nunc non odio dapibus scelerisque. Cum "
                                + "sociis natoque penatibus et magnis dis parturient "
                                + "montes, nascetur ridiculus mus. Mauris nec volutpat"
                                + " elit. Nulla eget diam nibh, ac lobortis nunc. Ut "
                                + "at lectus enim. Etiam quis ipsum dolor. Proin "
                                + "luctus lorem id purus venenatis imperdiet eget "
                                + "varius est. Morbi non lacinia neque. Maecenas "
                                + "lobortis ligula vel magna blandit pellentesque. ",
                            Calendar.getInstance().getTimeInMillis()),
                    new DropletContentModel(
                            1,
                            "Tester",
                            "Another test email",
                            "Email contents are supposed to be truncated after 50 characters, are the contents being truncated in the preview?",
                            Calendar.getInstance().getTimeInMillis()));

        DropletModel n =
            new DropletModel(
                    "Another",
                    new DropletContentModel(
                            1,
                            "Douglas Teoh",
                            "Just a test email",
                            "Lorem ipsum dolor sit amet, consectetur "
                                + "adipiscing elit. Etiam felis leo, elementum "
                                + "interdum interdum at, mollis sed nibh. Mauris "
                                + "laoreet metus sed massa sagittis tempor. "
                                + "Pellentesque diam est, adipiscing non vehicula id,"
                                + " aliquet eget ligula. Aenea scelerisque dictum"
                                + " nulla eget semper. Sed dapibus accumsan ligula "
                                + "eget faucibus. Donec vitae interdum neque. Aliquam"
                                + " sed erat vitae dui lacinia tincidunt a eu velit."
                                + " Suspendisse in nulla sit amet urna semper "
                                + "tincidunt pharetra at neque. Pellentesque et "
                                + "turpis eget urna aliquet elementum at sed massa. "
                                + "Fusce facilisis lorem id mi lobortis in rhoncus "
                                + "urna viverra. Praesent pulvinar volutpat urna."
                                + "\nCras molestie sollicitudin ultrices. In quis est"
                                + " sit amet dolor egestas interdum. Vestibulum "
                                + "consectetur enim ultricies nisl tristique varius. "
                                + "Etiam et nunc non odio dapibus scelerisque. Cum "
                                + "sociis natoque penatibus et magnis dis parturient "
                                + "montes, nascetur ridiculus mus. Mauris nec volutpat"
                                + " elit. Nulla eget diam nibh, ac lobortis nunc. Ut "
                                + "at lectus enim. Etiam quis ipsum dolor. Proin "
                                + "luctus lorem id purus venenatis imperdiet eget "
                                + "varius est. Morbi non lacinia neque. Maecenas "
                                + "lobortis ligula vel magna blandit pellentesque. ",
                            Calendar.getInstance().getTimeInMillis() - 50000),
                    new DropletContentModel(
                            1,
                            "Tester",
                            "Another test email",
                            "Email contents are supposed to be truncated after 50 characters, are the contents being truncated in the preview?",
                            Calendar.getInstance().getTimeInMillis() + 50000));
        // END

        view.displayDroplets(m, n);

        view.revalidate();
    }
    // private void askPassword() {
    // JPasswordField passwordField = new JPasswordField();
    // JLabel passwordLabel = new JLabel("Enter password:");
    // int option = JOptionPane.showConfirmDialog(null, new Object[] {
    // passwordLabel, passwordField }, "Plate",
    // JOptionPane.OK_CANCEL_OPTION);
    //
    // // User selected OK
    // if (option == 0) {
    // ac.addAccount(accountsField.getText(), new String(passwordField
    // .getPassword()));
    // accountsField.setText("");
    // passwordField.setText("");
    // }
    // }
}
