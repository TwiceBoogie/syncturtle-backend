package dev.twiceb.common.enums;

public enum UserRole {
    USER(15), ADMIN(20), SUPER_USER(25);

    public final int code;

    UserRole(int c) {
        this.code = c;
    }

    public static UserRole from(int c) {
        for (UserRole r : values()) {
            if (r.code == c) {
                return r;
            }
        }
        throw new IllegalArgumentException("Unkown role code: " + c);
    }
}
