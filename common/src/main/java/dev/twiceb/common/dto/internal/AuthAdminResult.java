package dev.twiceb.common.dto.internal;

import java.util.UUID;
import dev.twiceb.common.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthAdminResult {
    private UUID userId;
    private UserRole role;
}
