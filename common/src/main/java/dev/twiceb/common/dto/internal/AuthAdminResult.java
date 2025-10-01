package dev.twiceb.common.dto.internal;

import java.util.UUID;
import dev.twiceb.common.application.internal.bundle.IssuedTokens;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthAdminResult {
    private final UUID userId;
    private final IssuedTokens tokenGrant;
}
