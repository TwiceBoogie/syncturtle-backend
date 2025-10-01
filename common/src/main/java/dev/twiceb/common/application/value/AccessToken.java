package dev.twiceb.common.application.value;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccessToken {
    private final String jwt;
    private final Instant exp;
}
