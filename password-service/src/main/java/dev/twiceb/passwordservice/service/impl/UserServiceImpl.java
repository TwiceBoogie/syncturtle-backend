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

import static dev.twiceb.common.constants.ErrorMessage.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getAuthUser() {
        Long authUserId = AuthUtil.getAuthenticatedUserId();
        return userRepository.findById(authUserId)
                .orElseThrow(() -> new ApiRequestException(USER_NOT_FOUND, HttpStatus.UNAUTHORIZED));
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Long getUserIdByUsername(String username) {
        return userRepository.getUserByUsername(username.substring(1));
    }

    @Override
    public Boolean isUserExists(Long userId) {
        return userRepository.existsById(userId);
    }
}
