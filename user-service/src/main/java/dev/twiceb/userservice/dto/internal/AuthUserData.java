package dev.twiceb.userservice.dto.internal;

/**
 * provider agnostic payload. S2S only
 */
public final class AuthUserData {
    private final String email;
    private final UserData user;

    private AuthUserData(String email, UserData user) {
        this.email = email;
        this.user = user;
    }

    public static AuthUserData forEmailPassword(String email) {
        return new AuthUserData(email, UserData.builder().isPasswordAutoset(false).build());
    }

    public static AuthUserData forPasswordless(String email) {
        return new AuthUserData(email, UserData.builder().isPasswordAutoset(true).build());
    }

    public static AuthUserData of(String email, UserData user) {
        return new AuthUserData(email, user);
    }

    public String getEmail() {
        return email;
    }

    public UserData getUserData() {
        return user;
    }
}
