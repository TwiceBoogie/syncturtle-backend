package dev.twiceb.common.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminTokenGrant {
    private final UUID userId;
    private final TokenGrant tokenGrant;
}
