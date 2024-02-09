package dev.twiceb.userservice.mapper;

import dev.twiceb.common.dto.response.GenericResponse;
import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.userservice.dto.request.AuthenticationRequest;
import dev.twiceb.userservice.dto.response.AuthenticationResponse;
import dev.twiceb.userservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
@RequiredArgsConstructor
public class AuthenticationMapper {

    private final AuthenticationService authenticationService;
    private final BasicMapper mapper;

    public AuthenticationResponse login(AuthenticationRequest request, BindingResult bindingResult) {
        return mapper.convertToResponse(
                authenticationService.login(request, bindingResult), AuthenticationResponse.class
        );
    }

    public GenericResponse forgotPassword(String email, BindingResult bindingResult) {
        return mapper.convertToResponse(authenticationService.forgotPassword(email, bindingResult), GenericResponse.class);
    }
}
