package dev.twiceb.common.event;

public interface PasswordChangeLogEvent {
    Long getId();
    boolean isChangeSuccess();
    String getChangeResult();
    Long getUserDeviceId();
}
