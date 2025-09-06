package dev.twiceb.userservice.dto.internal;

public class UserData {
    private final String avatar;
    private final String firstName;
    private final String lastName;
    private final String providerId;
    private final boolean passwordAutoset;

    private UserData(Builder builder) {
        this.avatar = nvl(builder.avatar);
        this.firstName = nvl(builder.firstName);
        this.lastName = nvl(builder.lastName);
        this.providerId = nvl(builder.providerId);
        this.passwordAutoset = builder.passwordAutoset;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String avatar;
        private String firstName;
        private String lastName;
        private String providerId;
        private boolean passwordAutoset;

        public UserData build() {
            return new UserData(this);
        }

        public Builder withAvatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public Builder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder withProviderId(String providerId) {
            this.providerId = providerId;
            return this;
        }

        public Builder isPasswordAutoset(boolean passwordAutoset) {
            this.passwordAutoset = passwordAutoset;
            return this;
        }
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

    private static String nvl(String s) {
        return s == null ? "" : s;
    }
}
