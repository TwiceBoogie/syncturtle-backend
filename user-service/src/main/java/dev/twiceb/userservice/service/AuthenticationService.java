package dev.twiceb.userservice.service;

import dev.twiceb.common.dto.response.UserPrincipleResponse;
import dev.twiceb.userservice.dto.request.AuthenticationRequest;
import dev.twiceb.userservice.model.User;
import org.springframework.validation.BindingResult;

import java.util.Map;

public interface AuthenticationService {
    Long getAuthenticatedUserId();

    User getAuthenticatedUser();

    UserPrincipleResponse getUserPrincipleByEmail(String email);

    Map<String, Object> login(AuthenticationRequest request, BindingResult bindingResult);

    Map<String, String> getExistingEmail(String email);
}
