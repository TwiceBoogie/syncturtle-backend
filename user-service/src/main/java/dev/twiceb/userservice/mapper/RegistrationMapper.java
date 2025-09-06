package dev.twiceb.userservice.mapper;

import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.userservice.dto.request.AuthContextRequest;
import dev.twiceb.userservice.dto.request.MagicCodeRequest;
import dev.twiceb.userservice.dto.request.RegistrationRequest;
import dev.twiceb.userservice.dto.response.AuthenticationResponse;
import dev.twiceb.userservice.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrationMapper {

    private final RegistrationService registrationService;
    private final BasicMapper mapper;

    public AuthenticationResponse magicRegistration(AuthContextRequest<MagicCodeRequest> request) {
        return mapper.convertToResponse(registrationService.magicSignup(request),
                AuthenticationResponse.class);
    }

    public AuthenticationResponse signUp(AuthContextRequest<RegistrationRequest> request) {
        return mapper.convertToResponse(registrationService.signup(request),
                AuthenticationResponse.class);
    }
}
