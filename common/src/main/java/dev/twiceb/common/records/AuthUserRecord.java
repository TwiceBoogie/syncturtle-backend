package dev.twiceb.common.records;

import java.util.UUID;

public record AuthUserRecord(UUID id, String email, String firstName, String lastName) {
}
