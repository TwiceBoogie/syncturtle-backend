package dev.twiceb.common.dto.request;

import java.util.Map;
import java.util.Objects;
// builder pattern
// uri: https://java-design-patterns.com/patterns/builder/

/**
 * Represents an email request with specific attributes.
 * This class allows building an EmailRequest object with mandatory fields and
 * optional attributes.
 */
public final class EmailRequest {
    private final String to;
    private final String subject;
    private final String template;
    private final Map<String, Object> attributes;

    public EmailRequest() {
        this.to = null;
        this.subject = null;
        this.template = null;
        this.attributes = null;
    };

    /**
     * Constructs a new EmailRequest object with specified attributes.
     *
     * @param builder An instance of the Builder class.
     */
    private EmailRequest(Builder builder) {
        this.to = builder.to;
        this.subject = builder.subject;
        this.template = builder.template;
        this.attributes = builder.attributes;
    }

    /**
     * Builder for constructing an EmailRequest object.
     */
    public static class Builder {
        private final String to;
        private final String subject;
        private final String template;
        private Map<String, Object> attributes;

        public Builder(String to, String subject, String template) {
            this.to = to;
            this.subject = subject;
            this.template = template;
        }

        public Builder attributes(Map<String, Object> attributes) {
            this.attributes = attributes;
            return this;
        }

        public EmailRequest build() {
            return new EmailRequest(this);
        }
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getTemplate() {
        return template;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "EmailRequest{" +
                "to='" + to + '\'' +
                ", subject='" + subject + '\'' +
                ", template='" + template + '\'' +
                ", attributes=" + attributes +
                '}';
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((to == null) ? 0 : to.hashCode());
        result = PRIME * result + ((subject == null) ? 0 : subject.hashCode());
        result = PRIME * result + ((template == null) ? 0 : template.hashCode());
        result = PRIME * result + ((attributes == null) ? 0 : attributes.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof EmailRequest other))
            return false;
        // Ensure null safety by checking for null attributes
        return Objects.equals(this.to, other.to) &&
                Objects.equals(this.subject, other.subject) &&
                Objects.equals(this.template, other.template) &&
                Objects.equals(this.attributes, other.attributes);
    }
}
