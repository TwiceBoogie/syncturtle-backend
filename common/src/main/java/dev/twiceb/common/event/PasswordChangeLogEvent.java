package dev.twiceb.common.event;

import java.util.UUID;

public interface PasswordChangeLogEvent {
    UUID getId();

    boolean isChangeSuccess();

    String getChangeResult();

    UUID getUserDeviceId();
}
