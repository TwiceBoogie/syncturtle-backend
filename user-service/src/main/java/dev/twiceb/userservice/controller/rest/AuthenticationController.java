package dev.twiceb.userservice.controller.rest;

import dev.twiceb.common.dto.response.GenericResponse;
import dev.twiceb.userservice.controller.AuthenticationControllerSwagger;
import dev.twiceb.userservice.dto.request.*;
import dev.twiceb.userservice.dto.response.AuthUserResponse;
import dev.twiceb.userservice.dto.response.AuthenticationResponse;
import dev.twiceb.userservice.mapper.AuthenticationMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static dev.twiceb.common.constants.PathConstants.*;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping(UI_V1_AUTH)
public class AuthenticationController implements AuthenticationControllerSwagger {

    private final AuthenticationMapper authenticationMapper;

    @Override
    public ResponseEntity<AuthUserResponse> login(AuthenticationRequest request, BindingResult bindingResult) {
        AuthenticationResponse result = authenticationMapper.login(request, bindingResult);
        ResponseCookie jwtCookie = ResponseCookie.from("token", result.getToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofMillis(14400000))
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(result.getUser());
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
