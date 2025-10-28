package dev.twiceb.common.dto.internal;

import java.util.UUID;
import dev.twiceb.common.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthUserResult {
    private String redirectionPath;
    private UUID userId;
    private UserRole role;
}
