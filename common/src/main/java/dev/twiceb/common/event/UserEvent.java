package dev.twiceb.common.event;

import dev.twiceb.common.enums.UserRole;
import dev.twiceb.common.enums.UserStatus;

public interface UserEvent {
    Long getId();
    String getFullName();
    String getUsername();
    UserStatus getUserStatus();
    UserRole getRole();
}
