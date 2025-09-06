package dev.twiceb.common.dto.internal;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthUserResult {
    private final UUID userId;
    private final String email;
    private final String firstName;
    private final String lastName;
}
