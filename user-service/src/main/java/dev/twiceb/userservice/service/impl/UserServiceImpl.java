package dev.twiceb.userservice.service.impl;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.userservice.domain.model.*;
import dev.twiceb.userservice.domain.projection.UserDeviceProjection;
import dev.twiceb.userservice.domain.repository.UserRepository;
import dev.twiceb.userservice.service.AuthenticationService;
import dev.twiceb.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static dev.twiceb.common.constants.ErrorMessage.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiRequestException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDeviceProjection getUserDeviceDetails(UUID userId) {
        return null;
    }

    @Override
    public Map<String, String> updateUserProfile(User userInfo) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateUserProfile'");
    }


}
