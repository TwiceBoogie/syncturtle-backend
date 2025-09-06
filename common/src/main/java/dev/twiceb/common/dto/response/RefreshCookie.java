package dev.twiceb.common.dto.response;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshCookie {
    private final String token;
    private final Instant exp;
}
