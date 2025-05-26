package dev.twiceb.taskservice.service.impl;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.util.AuthUtil;
import dev.twiceb.taskservice.model.User;
import dev.twiceb.taskservice.repository.UserRepository;
import dev.twiceb.taskservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static dev.twiceb.common.constants.ErrorMessage.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getAuthUser() {
        UUID authUserId = AuthUtil.getAuthenticatedUserId();
        return userRepository.findById(authUserId)
                .orElseThrow(() -> new ApiRequestException(USER_NOT_FOUND, HttpStatus.UNAUTHORIZED));
    }

    @Override
    public Optional<User> getUserById(UUID userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return Optional.empty();
    }
}
