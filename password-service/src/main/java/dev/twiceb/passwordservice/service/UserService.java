package dev.twiceb.passwordservice.service;

import dev.twiceb.passwordservice.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User getAuthUser();
    Optional<User> getUserById(UUID userId);
    Long getUserIdByUsername(String username);
    Boolean isUserExists(UUID userId);
}
