package dev.twiceb.userservice.service;

import dev.twiceb.common.records.AuthenticatedUserRecord;
import dev.twiceb.userservice.dto.request.*;
import org.springframework.validation.BindingResult;

import java.util.Map;

public interface RegistrationService {
    Map<String, String> registration(RegistrationRequest request, BindingResult bindingResult);

    Map<String, String> sendRegistrationCode(String email, BindingResult bindingResult);

    Map<String, Object> checkRegistrationCode(String code);

    AuthenticatedUserRecord magicRegistration(MagicCodeRequest request,
            BindingResult bindingResult);

    AuthenticatedUserRecord signUp(AuthContextRequest<RegistrationRequest> request);
}
