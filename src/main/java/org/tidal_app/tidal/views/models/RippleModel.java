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

package org.tidal_app.tidal.views.models;

public class RippleModel {

    private transient final Object id;
    private transient final String origin;
    private transient final String subject;
    private transient final String content;
    private transient final long received;

    public Object getId() {
        return id;
    }

    public String getOrigin() {
        return origin;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public long getReceived() {
        return received;
    }

    /**
     * @param id
     *            Ripple identifier
     * @param origin
     *            Ripple origin
     * @param subject
     *            Ripple subject
     * @param content
     *            Ripple contents
     * @param received
     *            Ripple receipt time in milliseconds
     */
    public RippleModel(final Object id, final String origin,
            final String subject, final String content, final long received) {
        this.id = id;
        this.origin = origin;
        this.subject = subject;
        this.content = content;
        this.received = received;
    }
}
