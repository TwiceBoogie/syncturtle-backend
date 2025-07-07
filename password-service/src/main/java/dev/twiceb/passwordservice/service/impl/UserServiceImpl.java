package dev.twiceb.passwordservice.service.impl;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.util.AuthUtil;
import dev.twiceb.passwordservice.model.User;
import dev.twiceb.passwordservice.repository.UserRepository;
import dev.twiceb.passwordservice.service.UserService;
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
    public Long getUserIdByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    @Override
    public Boolean isUserExists(UUID userId) {
        return userRepository.existsById(userId);
    }
}
