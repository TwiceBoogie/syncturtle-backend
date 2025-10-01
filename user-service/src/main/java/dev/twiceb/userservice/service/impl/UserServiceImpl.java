package dev.twiceb.userservice.service.impl;

import dev.twiceb.common.dto.context.AuthContext;
import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.userservice.domain.model.*;
import dev.twiceb.userservice.domain.projection.ProfileProjection;
import dev.twiceb.userservice.domain.projection.UserDeviceProjection;
import dev.twiceb.userservice.domain.projection.UserMeProjection;
import dev.twiceb.userservice.domain.repository.ProfileRepository;
import dev.twiceb.userservice.domain.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

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

    @Override
    @Transactional(readOnly = true)
    public ProfileProjection getProfile() {
        return profileRepository.findByUser_Id(AuthContext.get()).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public UserMeProjection getUser() {
        return userRepository.getUserById(AuthContext.get(), UserMeProjection.class)
                .orElseThrow(() -> new ApiRequestException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

}
