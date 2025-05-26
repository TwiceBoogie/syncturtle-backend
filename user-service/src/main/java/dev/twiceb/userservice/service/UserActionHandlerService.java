package dev.twiceb.userservice.service;

import dev.twiceb.common.event.PasswordChangeEvent;

import java.util.UUID;

public interface UserActionHandlerService {
    void handlePasswordChangeEvent(PasswordChangeEvent event, UUID authUserId);
}
