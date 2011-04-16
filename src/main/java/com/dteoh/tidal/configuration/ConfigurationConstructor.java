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

import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

import com.dteoh.tidal.configuration.models.Configuration;

/**
 * Custom constructor for the main program's configuration file.
 * 
 * @author Douglas Teoh
 * 
 */
public final class ConfigurationConstructor extends Constructor {

    public ConfigurationConstructor() {
        super();
        yamlConstructors.put(new Tag("!config"), new ConstructConfiguration());
    }

    private final class ConstructConfiguration extends AbstractConstruct {

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.yaml.snakeyaml.constructor.Construct#construct(org.yaml.snakeyaml
         * .nodes.Node)
         */
        @SuppressWarnings("unchecked")
        @Override
        public Object construct(final Node node) {
            final Map enc = constructMapping((MappingNode) node);

            final String digest = (String) enc.get("digest");

            return new Configuration(digest);
        }

    }

}
