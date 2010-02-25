package net.teohdouglas.tidal.sources.gmail;

import net.teohdouglas.tidal.exceptions.DropletInitException;
import net.teohdouglas.tidal.impl.sources.gmail.GmailDroplet;

import org.junit.Test;

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
