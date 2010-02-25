package net.teohdouglas.tidal.sources;

import java.util.List;

import net.teohdouglas.tidal.exceptions.DropletInitException;
import net.teohdouglas.tidal.sources.models.EmailRipple;

public abstract class EmailDroplet implements Droplet {

	protected final String username;
	protected final String password;

	public EmailDroplet(final String username, final String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public abstract void init() throws DropletInitException;

	public abstract void destroy();

	public abstract List<EmailRipple> getRipples();

}
