package dev.twiceb.userservice.service.impl;

import dev.twiceb.common.dto.response.UserDeviceResponse;
import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.userservice.domain.projection.UserPrincipalProjection;
import dev.twiceb.userservice.domain.repository.UserRepository;
import dev.twiceb.userservice.service.UserClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserClientServiceImpl implements UserClientService {

    private final UserRepository userRepository;

    @Override
    public String getUserEmail(UUID userId) {
        return userRepository.getUserEmail(userId);
    }

    @Override
    public void increaseNotificationCount(UUID userId) {
        userRepository.increaseNotificationCount(userId);
    }

    @Override
    public void decreaseNotificationCount(UUID userId) {
        userRepository.decreaseNotificationCount(userId);
    }

    @Override
    public void resetNotificationCount(UUID userId) {
        userRepository.resetNotificationCount(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDeviceResponse getUserDevice(UUID userId) {

        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public UserPrincipalProjection getUserPrincipal(UUID userId) {
        return userRepository.getUserById(userId, UserPrincipalProjection.class)
                .orElseThrow(() -> new ApiRequestException("user.not_found", HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public UUID findUserIdByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ApiRequestException("Email is not valid", HttpStatus.BAD_REQUEST);
        }
        email = email.toLowerCase().trim();
        return userRepository.findIdByEmail(email);
    }
}
