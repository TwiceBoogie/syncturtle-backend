package dev.twiceb.userservice.mapper;

import dev.twiceb.common.dto.response.GenericResponse;
import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.userservice.dto.request.AuthContextRequest;
import dev.twiceb.userservice.dto.request.MagicCodeRequest;
import dev.twiceb.userservice.dto.request.RegistrationRequest;
import dev.twiceb.userservice.dto.response.AuthenticationResponse;
import dev.twiceb.userservice.dto.response.RegistrationEndResponse;
import dev.twiceb.userservice.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
@RequiredArgsConstructor
public class RegistrationMapper {

    private final RegistrationService registrationService;
    private final BasicMapper mapper;

    public GenericResponse registration(RegistrationRequest request, BindingResult bindingResult) {
        return mapper.convertToResponse(registrationService.registration(request, bindingResult),
                GenericResponse.class);
    }

    public GenericResponse sendRegistrationCode(String email, BindingResult bindingResult) {
        return mapper.convertToResponse(
                registrationService.sendRegistrationCode(email, bindingResult),
                GenericResponse.class);
    }

    public AuthenticationResponse magicRegistration(MagicCodeRequest request,
            BindingResult bindingResult) {
        return mapper.convertToResponse(
                registrationService.magicRegistration(request, bindingResult),
                AuthenticationResponse.class);
    }

    public RegistrationEndResponse checkRegistrationCode(String code) {
        return mapper.convertToResponse(registrationService.checkRegistrationCode(code),
                RegistrationEndResponse.class);
    }

    public AuthenticationResponse signUp(AuthContextRequest<RegistrationRequest> request) {
        return mapper.convertToResponse(registrationService.signUp(request),
                AuthenticationResponse.class);
    }
}
