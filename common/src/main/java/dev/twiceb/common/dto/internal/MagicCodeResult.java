package dev.twiceb.common.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class MagicCodeResult {
    private final boolean existing;
    private final String status;
}
