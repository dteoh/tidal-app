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

import org.jasypt.util.text.StrongTextEncryptor;
import org.tidal_app.tidal.sources.email.models.EmailSettings;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

/**
 * This class contains all custom constructors for the various Droplets.
 * 
 * @author Douglas Teoh
 */
public class DropletsConfigurationsConstructor extends Constructor {

    private StrongTextEncryptor decryptor;

    public DropletsConfigurationsConstructor(final StrongTextEncryptor decryptor) {
        this.decryptor = decryptor;
        yamlConstructors.put(new Tag("!email"), new ConstructEmailSettings());
    }

    /**
     * Used to re-create an {@link EmailSettings} object.
     * 
     * @author Douglas Teoh
     */
    private class ConstructEmailSettings extends AbstractConstruct {
        /*
         * (non-Javadoc)
         * @see
         * org.yaml.snakeyaml.constructor.Construct#construct(org.yaml.snakeyaml
         * .nodes.Node)
         */
        @SuppressWarnings("unchecked")
        @Override
        public Object construct(final Node node) {
            Map enc = constructMapping((MappingNode) node);

            String host = (String) enc.get("host");
            if (host == null) {
                return null;
            }

            String protocol = (String) enc.get("prot");
            if (protocol == null) {
                return null;
            }

            String username = (String) enc.get("user");
            if (username == null) {
                return null;
            } else {
                username = decryptor.decrypt(username);
            }

            String password = (String) enc.get("pass");
            if (password == null) {
                return null;
            } else {
                password = decryptor.decrypt(password);
            }

            EmailSettings settings = new EmailSettings();
            settings.setHost(host);
            settings.setProtocol(protocol);
            settings.setUsername(username);
            settings.setPassword(password);
            return settings;
        }

    }

}
