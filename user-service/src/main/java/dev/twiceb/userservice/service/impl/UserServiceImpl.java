package dev.twiceb.userservice.service.impl;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.userservice.model.*;
import dev.twiceb.userservice.repository.UserRepository;
import dev.twiceb.userservice.repository.projection.UserDeviceProjection;
import dev.twiceb.userservice.service.AuthenticationService;
import dev.twiceb.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static dev.twiceb.common.constants.ErrorMessage.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiRequestException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDeviceProjection getUserDeviceDetails(Long userId) {
        return null;
    }

    @Override
    @Transactional
    public Map<String, String> updateUserProfile(User userInfo) {
        User user = authenticationService.getAuthenticatedUser();

        if (userInfo.getFirstName().isEmpty() || userInfo.getFirstName().length() > 36) {
            throw new ApiRequestException("INCO", HttpStatus.BAD_REQUEST);
        }
        if (userInfo.getLastName().isEmpty() || userInfo.getLastName().length() > 36) {
            throw new ApiRequestException("inco", HttpStatus.BAD_REQUEST);
        }

        user.setFirstName(userInfo.getFirstName());
        user.setLastName(userInfo.getLastName());
        user.setAbout(userInfo.getAbout());
        user.setGender(user.getGender());

        return null;
    }

}
