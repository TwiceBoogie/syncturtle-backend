package dev.twiceb.taskservice.service;

import dev.twiceb.taskservice.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    User getAuthUser();
    Optional<User> getUserById(UUID userId);
    Optional<User> getUserByUsername(String username);
}
