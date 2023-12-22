package dev.twiceb.userservice.service.impl;

import dev.twiceb.common.dto.response.UserPrincipleResponse;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.security.JwtProvider;
import dev.twiceb.userservice.dto.request.AuthenticationRequest;
import dev.twiceb.userservice.model.User;
import dev.twiceb.userservice.repository.UserRepository;
import dev.twiceb.userservice.repository.projection.AuthUserProjection;
import dev.twiceb.userservice.repository.projection.UserPrincipalProjection;
import dev.twiceb.userservice.service.AuthenticationService;
import dev.twiceb.userservice.service.util.UserServiceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Map;

import static dev.twiceb.common.constants.ErrorMessage.INCORRECT_PASSWORD;
import static dev.twiceb.common.constants.ErrorMessage.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final UserServiceHelper userServiceHelper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public Long getAuthenticatedUserId() {
        return null;
    }

    @Override
    public User getAuthenticatedUser() {
        return null;
    }

    @Override
    public UserPrincipalProjection getUserPrincipleByEmail(String email) {
        return userRepository.getUserByEmail(email, UserPrincipalProjection.class)
                .orElseThrow(() -> new ApiRequestException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    @Override
    public Map<String, Object> login(AuthenticationRequest request, BindingResult bindingResult) {
        userServiceHelper.processInputErrors(bindingResult);
        AuthUserProjection user = userRepository.getUserByEmail(request.getEmail(), AuthUserProjection.class)
                .orElseThrow(
                        () -> new ApiRequestException(USER_NOT_FOUND, HttpStatus.BAD_REQUEST));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiRequestException(INCORRECT_PASSWORD, HttpStatus.BAD_REQUEST);
        }

        String token = jwtProvider.createToken(user.getEmail(), user.getRole());

        return Map.of("user", user, "token", token);
    }

    @Override
    public Map<String, String> getExistingEmail(String email) {

        return null;
    }
}
