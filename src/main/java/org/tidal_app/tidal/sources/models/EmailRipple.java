package org.tidal_app.tidal.sources.models;

/**
 * Am EmailRipple represents an email message.
 * 
 * @author douglas
 */
public class EmailRipple implements Comparable<EmailRipple> {

    private final int id;
    private final String sender;
    private final String subject;
    private final String content;
    private final long receivedDate;

    public int getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getSubject() {
        return subject;
    }

    public long getReceivedDate() {
        return receivedDate;
    }

    public String getContent() {
        return content;
    }

    /**
     * Creates a new email ripple
     * 
     * @param id
     *            message identifier
     * @param sender
     *            name and/or email address of sender
     * @param subject
     *            subject of email
     * @param content
     *            email contents
     * @param receivedDate
     *            when was the email received
     */
    public EmailRipple(final int id, final String sender, final String subject,
            final String content, final long receivedDate) {
        super();
        this.id = id;
        this.sender = sender;
        this.subject = subject;
        this.content = content;
        this.receivedDate = receivedDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        result = prime * result + id;
        result = prime * result + (int) (receivedDate ^ (receivedDate >>> 32));
        result = prime * result + ((sender == null) ? 0 : sender.hashCode());
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
        EmailRipple other = (EmailRipple) obj;
        if (content == null) {
            if (other.content != null) {
                return false;
            }
        } else if (!content.equals(other.content)) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        if (receivedDate != other.receivedDate) {
            return false;
        }
        if (sender == null) {
            if (other.sender != null) {
                return false;
            }
        } else if (!sender.equals(other.sender)) {
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

    @Override
    public int compareTo(final EmailRipple other) {
        // Want newer messages first.
        if (receivedDate < other.receivedDate) {
            return 1;
        }
        if (receivedDate > other.receivedDate) {
            return -1;
        }
        if (id < other.id) {
            return -1;
        }
        if (id > other.id) {
            return 1;
        }
        return 0;
    }

}
