package dev.twiceb.userservice.dto.internal;

import java.util.UUID;
import dev.twiceb.common.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthUserResult {
    private final String redirectionPath;
    private final UUID userId;
    private final UserRole role;
}
