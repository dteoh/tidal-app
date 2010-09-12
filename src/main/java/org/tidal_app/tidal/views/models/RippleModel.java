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

public final class RippleModel implements Comparable<RippleModel> {

    public static class Builder {
        private final Object id;
        private String origin;
        private String subject;
        private String content;
        private long received;

        public Builder(final Object id) {
            this.id = id;
        }

        public Builder origin(final String origin) {
            this.origin = origin;
            return this;
        }

        public Builder subject(final String subject) {
            this.subject = subject;
            return this;
        }

        public Builder content(final String content) {
            this.content = content;
            return this;
        }

        public Builder received(final long received) {
            this.received = received;
            return this;
        }

        public RippleModel build() {
            return new RippleModel(id, origin, subject, content, received);
        }
    }

    private final Object id;
    private final String origin;
    private final String subject;
    private final String content;
    private final long received;

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((origin == null) ? 0 : origin.hashCode());
        result = prime * result + (int) (received ^ (received >>> 32));
        result = prime * result + ((subject == null) ? 0 : subject.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RippleModel other = (RippleModel) obj;
        if (content == null) {
            if (other.content != null) {
                return false;
            }
        } else if (!content.equals(other.content)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (origin == null) {
            if (other.origin != null) {
                return false;
            }
        } else if (!origin.equals(other.origin)) {
            return false;
        }
        if (received != other.received) {
            return false;
        }
        if (subject == null) {
            if (other.subject != null) {
                return false;
            }
        } else if (!subject.equals(other.subject)) {
            return false;
        }
        return true;
    }

    /**
     * Compares based on received date (newest first) then subject.
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final RippleModel other) {
        if (other == null) {
            return -1;
        }

        if (getReceived() < other.getReceived()) {
            return 1;
        }

        if (getReceived() > other.getReceived()) {
            return -1;
        }

        return getSubject().compareTo(other.getSubject());
    }

}
