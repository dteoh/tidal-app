package net.teohdouglas.tidal.sources;

import net.teohdouglas.tidal.exceptions.DropletInitException;

/**
 * A Droplet represents some arbitrary source of communications. Each Droplet
 * causes a Ripple to occur.
 * 
 * @author douglas
 */
public interface Droplet {

	public void init() throws DropletInitException;

	public void destroy();

}
