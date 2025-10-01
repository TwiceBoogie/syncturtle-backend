package dev.twiceb.common.dto.response;

import java.time.Instant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InstanceStatusResult {
    private final boolean setupDone;
    private final String edition;
    private final Instant updatedAt;
}
