package org.tidal_app.tidal.sources.gmail;


import org.junit.Test;
import org.tidal_app.tidal.exceptions.DropletInitException;
import org.tidal_app.tidal.impl.sources.gmail.GmailDroplet;

public class GmailDropletTests {

    /**
     * No email-password pair specified, use own. Do not commit.
     * 
     * @throws DropletInitException
     */
    @Test
    public void testGetRipples() throws DropletInitException {
        GmailDroplet d = new GmailDroplet("", "");

        d.init();
        d.getRipples();
        d.destroy();
    }

}
