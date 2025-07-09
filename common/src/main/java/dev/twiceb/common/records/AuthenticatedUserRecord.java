package dev.twiceb.common.records;

public record AuthenticatedUserRecord(AuthUserRecord user, String token, String deviceToken) {
}
