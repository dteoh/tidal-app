package org.tidal_app.tidal.sources;

import org.tidal_app.tidal.exceptions.DropletInitException;

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
