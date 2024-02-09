package dev.twiceb.userservice.mapper;

import dev.twiceb.common.dto.response.GenericResponse;
import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.userservice.dto.request.AuthenticationRequest;
import dev.twiceb.userservice.dto.request.ProcessEmailRequest;
import dev.twiceb.userservice.dto.request.RegistrationRequest;
import dev.twiceb.userservice.dto.response.AuthenticationResponse;
// import dev.twiceb.userservice.service.AuthenticationService;
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

    public GenericResponse sendRegistrationCode(ProcessEmailRequest request, BindingResult bindingResult) {
        return mapper.convertToResponse(registrationService.sendRegistrationCode(request, bindingResult),
                GenericResponse.class);
    }

    public RegistrationEndResponse checkRegistrationCode(String code) {
        return mapper.convertToResponse(registrationService.checkRegistrationCode(code), RegistrationEndResponse.class);
    }

    public AuthenticationResponse endRegistration(AuthenticationRequest request, BindingResult bindingResult) {
        return mapper.convertToResponse(registrationService.endRegistration(request, bindingResult),
                AuthenticationResponse.class);
    }
}
