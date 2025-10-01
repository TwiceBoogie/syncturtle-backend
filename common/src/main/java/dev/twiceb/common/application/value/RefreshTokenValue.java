package dev.twiceb.common.application.value;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshTokenValue {
    private final String token;
    private final Instant exp;
}
