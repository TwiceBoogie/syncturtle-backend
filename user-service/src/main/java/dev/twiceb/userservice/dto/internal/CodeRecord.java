package dev.twiceb.userservice.dto.internal;

import lombok.Getter;

@Getter
public class CodeRecord {
    private String email;
    private String tokenHash;
    private int currentAttempt;

    protected CodeRecord() {}

    private CodeRecord(String email, String tokenHash, int currentAttempt) {
        this.email = email;
        this.tokenHash = tokenHash;
        this.currentAttempt = currentAttempt;
    }

    public static CodeRecord first(String email, String tokenHash) {
        return new CodeRecord(email, tokenHash, 0);
    }

    public CodeRecord bumpAttempt() {
        return new CodeRecord(email, tokenHash, currentAttempt + 1);
    }

    public CodeRecord rotate(String newHash) {
        return new CodeRecord(email, newHash, currentAttempt + 1);
    }
}
