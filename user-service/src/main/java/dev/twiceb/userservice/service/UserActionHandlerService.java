package dev.twiceb.userservice.service;

import dev.twiceb.common.event.PasswordChangeEvent;

public interface UserActionHandlerService {
    void handlePasswordChangeEvent(PasswordChangeEvent event, Long authUserId);
}
