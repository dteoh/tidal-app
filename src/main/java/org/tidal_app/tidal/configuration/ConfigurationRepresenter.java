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

package org.tidal_app.tidal.configuration;

import java.util.Map;
import java.util.TreeMap;

import org.tidal_app.tidal.configuration.models.Configuration;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

/**
 * Custom representer for the main program's configuration file.
 * 
 * @author Douglas Teoh
 * 
 */
public final class ConfigurationRepresenter extends Representer {

    public ConfigurationRepresenter() {
        super();
        representers.put(Configuration.class, new RepresentConfiguration());
    }

    private final class RepresentConfiguration implements Represent {

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
            final Configuration config = (Configuration) obj;
            final Map enc = new TreeMap();
            enc.put("digest", config.getAuthKeyDigest());
            return representMapping(new Tag("!config"), enc, true);
        }

    }

}
