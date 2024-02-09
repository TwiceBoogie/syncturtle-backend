package dev.twiceb.userservice.service;

import dev.twiceb.userservice.dto.request.AuthenticationRequest;
import dev.twiceb.userservice.dto.request.ProcessEmailRequest;
import dev.twiceb.userservice.dto.request.RegistrationRequest;
import org.springframework.validation.BindingResult;

import java.util.Map;

public interface RegistrationService {
    Map<String, String> registration(RegistrationRequest request, BindingResult bindingResult);

    Map<String, String> sendRegistrationCode(ProcessEmailRequest request, BindingResult bindingResult);

    Map<String, Object> checkRegistrationCode(String code);

    Map<String, Object> endRegistration(AuthenticationRequest request, BindingResult bindingResult);
}
