package dev.twiceb.passwordservice.service;

import dev.twiceb.passwordservice.model.User;

import java.util.Optional;

public interface UserService {
    User getAuthUser();
    Optional<User> getUserById(Long userId);
    Long getUserIdByUsername(String username);
    Boolean isUserExists(Long userId);
}
