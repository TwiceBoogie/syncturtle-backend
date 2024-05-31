package dev.twiceb.userservice.controller.rest;

import dev.twiceb.common.dto.response.GenericResponse;
import dev.twiceb.userservice.controller.AuthenticationControllerSwagger;
import dev.twiceb.userservice.dto.request.*;
import dev.twiceb.userservice.dto.response.AuthenticationResponse;
import dev.twiceb.userservice.mapper.AuthenticationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static dev.twiceb.common.constants.PathConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(UI_V1_AUTH)
public class AuthenticationController implements AuthenticationControllerSwagger {

    private final AuthenticationMapper authenticationMapper;

    @Override
    public ResponseEntity<AuthenticationResponse> login(AuthenticationRequest request, BindingResult bindingResult) {
        return ResponseEntity.ok(authenticationMapper.login(request, bindingResult));
    }

    @Override
    public ResponseEntity<GenericResponse> forgotUsername(ProcessEmailRequest request, BindingResult bindingResult) {
        return ResponseEntity.ok(authenticationMapper.forgotUsername(request.getEmail(), bindingResult));
    }

    @Override
    public ResponseEntity<GenericResponse> forgotPassword(ProcessEmailRequest request, BindingResult bindingResult) {
        return ResponseEntity.ok(authenticationMapper.forgotPassword(request.getEmail(), bindingResult));
    }

    @Override
    public ResponseEntity<GenericResponse> verifyOtp(PasswordOtpRequest request, BindingResult bindingResult) {
        return ResponseEntity.ok(authenticationMapper.verifyOtp(request.getOtp(), bindingResult));
    }

    @Override
    public ResponseEntity<GenericResponse> resetPassword(PasswordResetRequest request,
                                                         @PathVariable("token") String token,
                                                         BindingResult bindingResult) {
        return ResponseEntity.ok(authenticationMapper.resetPassword(request, token, bindingResult));
    }

    @Override
    public ResponseEntity<AuthenticationResponse> verifyDeviceToken(@PathVariable("token") String token,
                                                                    @RequestParam("trust") boolean trust) {
        return ResponseEntity.ok(authenticationMapper.verifyDeviceVerification(token, trust));
    }
}
