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

package com.dteoh.tidal.configuration;

import java.util.Map;
import java.util.TreeMap;

import org.jasypt.util.text.StrongTextEncryptor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import com.dteoh.tidal.sources.email.models.EmailSettings;

/**
 * This class contains all custom representers for the various Droplets.
 * 
 * @author Douglas Teoh
 */
public final class ConfigurablesRepresenter extends Representer {

    private final StrongTextEncryptor encryptor;

    public ConfigurablesRepresenter(final StrongTextEncryptor encryptor) {
        super();
        this.encryptor = encryptor;
        representers.put(EmailSettings.class, new RepresentEmailSettings());
    }

    /**
     * Used to serialize an {@link EmailSettings} object.
     * 
     * @author Douglas Teoh
     */
    private final class RepresentEmailSettings implements Represent {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.yaml.snakeyaml.representer.Represent#representData(java.lang.
         * Object)
         */
        @SuppressWarnings("unchecked")
        @Override
        public Node representData(final Object obj) {
            final EmailSettings settings = (EmailSettings) obj;
            final Map enc = new TreeMap();
            enc.put("host", settings.getHost());
            enc.put("prot", settings.getProtocol() != null ? settings
                    .getProtocol().toString() : null);
            enc.put("user", encryptor.encrypt(settings.getUsername()));
            enc.put("pass", encryptor.encrypt(settings.getPassword()));
            return representMapping(new Tag("!email"), enc, true);
        }
    }

}
