package dev.twiceb.userservice.application.internal.params;

import java.util.Objects;

public final class RegistrationDraft {
    private final String avatar;
    private final String firstName;
    private final String lastName;
    private final String providerId;
    private final boolean passwordAutoset;

    private RegistrationDraft(Builder b) {
        this.avatar = nvl(b.avatar);
        this.firstName = nvl(b.firstName);
        this.lastName = nvl(b.lastName);
        this.providerId = nvl(b.providerId);
        this.passwordAutoset = b.passwordAutoset;
    }

    public static Builder builder() {
        return new Builder();
    }

    // create a builder pre-populated from this instance
    public Builder toBuilder() {
        return new Builder().avatar(avatar).firstName(firstName).lastName(lastName)
                .providerId(providerId).passwordAutoset(passwordAutoset);
    }

    public String getAvatar() {
        return avatar;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProviderId() {
        return providerId;
    }

    public boolean isPasswordAutoset() {
        return passwordAutoset;
    }

    // nvl = null value
    private static String nvl(String s) {
        return s == null ? "" : s;
    }

    public static RegistrationDraft empty() {
        return builder().passwordAutoset(false).build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RegistrationDraft))
            return false;
        RegistrationDraft that = (RegistrationDraft) o;
        return Objects.equals(avatar, that.avatar) && Objects.equals(firstName, that.firstName)
                && Objects.equals(lastName, that.lastName)
                && Objects.equals(providerId, that.providerId)
                && Objects.equals(passwordAutoset, that.passwordAutoset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(avatar, firstName, lastName, providerId, passwordAutoset);
    }

    // remember
    @Override
    public String toString() {
        return "RegistrationDraft{avatar='" + avatar + '\'' + ", firstName='" + firstName + '\''
                + ", lastName='" + lastName + '\'' + ", providerId='" + providerId + '\''
                + ", passwordAutoSet=" + passwordAutoset + '}';
    }

    public static final class Builder {
        private String avatar;
        private String firstName;
        private String lastName;
        private String providerId;
        private boolean passwordAutoset;

        private Builder() {}

        public Builder avatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder providerId(String providerId) {
            this.providerId = providerId;
            return this;
        }

        public Builder passwordAutoset(boolean passwordAutoset) {
            this.passwordAutoset = passwordAutoset;
            return this;
        }

        public RegistrationDraft build() {
            // no requiered fields
            return new RegistrationDraft(this);
        }
    }
}
