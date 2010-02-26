package org.tidal_app.tidal.views.models;

public class DropletContentModel {

    private final Object id;
    private final String origin;
    private final String subject;
    private final String content;
    private final long received;

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

    public DropletContentModel(final Object id, final String origin,
            final String subject, final String content, final long received) {
        this.id = id;
        this.origin = origin;
        this.subject = subject;
        this.content = content;
        this.received = received;
    }
}
