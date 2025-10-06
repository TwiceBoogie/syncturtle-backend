package dev.twiceb.userservice.application.internal.params;

import java.util.Objects;
import dev.twiceb.common.util.StringHelper;

/**
 * provider agnostic payload. internal S2S only
 */
public final class AuthSubjectParams {
    private final String email;
    private final RegistrationDraft draft;

    private AuthSubjectParams(Builder b) {
        this.email = b.email;
        this.draft = b.draft;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static AuthSubjectParams forEmailPassword(String email) {
        return builder().email(email)
                .draft(RegistrationDraft.builder().passwordAutoset(false).build()).build();
    }

    public static AuthSubjectParams forPasswordless(String email) {
        return builder().email(email)
                .draft(RegistrationDraft.builder().passwordAutoset(true).build()).build();
    }

    public String getEmail() {
        return email;
    }

    public RegistrationDraft getDraft() {
        return draft;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AuthSubjectParams))
            return false;
        AuthSubjectParams that = (AuthSubjectParams) o;
        return Objects.equals(email, that.email) && Objects.equals(draft, that.draft);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, draft);
    }

    @Override
    public String toString() {
        return "AuthSubjectParams{email='" + email + "', draft=" + draft + "}";
    }

    public static final class Builder {
        private String email;
        private RegistrationDraft draft;

        private Builder() {}

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder draft(RegistrationDraft draft) {
            this.draft = draft;
            return this;
        }

        public AuthSubjectParams build() {
            if (StringHelper.isBlank(email)) {
                throw new IllegalStateException("email must be set");
            }
            return new AuthSubjectParams(this);
        }
    }
}
