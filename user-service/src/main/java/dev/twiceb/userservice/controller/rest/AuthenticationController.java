package dev.twiceb.userservice.controller.rest;

import dev.twiceb.common.dto.response.GenericResponse;
import dev.twiceb.userservice.controller.AuthenticationControllerSwagger;
import dev.twiceb.userservice.dto.request.*;
import dev.twiceb.userservice.dto.response.AuthenticationResponse;
import dev.twiceb.userservice.dto.response.MagicCodeResponse;
import dev.twiceb.userservice.dto.response.MagicKeyResponse;
import dev.twiceb.userservice.mapper.AuthenticationMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static dev.twiceb.common.constants.PathConstants.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping(UI_V1_AUTH)
public class AuthenticationController implements AuthenticationControllerSwagger {

    private final AuthenticationMapper authenticationMapper;

    @PostMapping(CHECK_EMAIL)
    public ResponseEntity<MagicCodeResponse> checkEmail(@RequestBody MagicCodeRequest request,
            BindingResult bindingResult) {
        return ResponseEntity.ok(authenticationMapper.checkEmail(request, bindingResult));
    }

    @PostMapping(GENERATE_MAGIC_CODE)
    public ResponseEntity<MagicKeyResponse> generateMagicCode(@RequestBody MagicCodeRequest request,
            BindingResult bindingResult) {
        return ResponseEntity
                .ok(authenticationMapper.generateMagicCodeAuth(request, bindingResult));
    }

    @PostMapping(MAGIC_LOGIN)
    public ResponseEntity<AuthenticationResponse> magicLogin(@RequestBody MagicCodeRequest request,
            BindingResult bindingResult) {
        return ResponseEntity.ok(authenticationMapper.magicLogin(request, bindingResult));
    }

    @Override
    @PostMapping(LOGIN)
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request,
            BindingResult bindingResult) {
        return ResponseEntity.ok(authenticationMapper.login(request, bindingResult));
    }

    @Override
    @PostMapping(FORGOT_USERNAME)
    public ResponseEntity<GenericResponse> forgotUsername(@RequestBody ProcessEmailRequest request,
            BindingResult bindingResult) {
        return ResponseEntity
                .ok(authenticationMapper.forgotUsername(request.getEmail(), bindingResult));
    }

    @Override
    @PostMapping(FORGOT_PASSWORD)
    public ResponseEntity<GenericResponse> forgotPassword(@RequestBody ProcessEmailRequest request,
            BindingResult bindingResult) {
        return ResponseEntity
                .ok(authenticationMapper.forgotPassword(request.getEmail(), bindingResult));
    }

    @Override
    @PostMapping(VERIFY_OTP)
    public ResponseEntity<GenericResponse> verifyOtp(@RequestBody PasswordOtpRequest request,
            BindingResult bindingResult) {
        return ResponseEntity.ok(authenticationMapper.verifyOtp(request.getOtp(), bindingResult));
    }

    @Override
    @PostMapping(RESET)
    public ResponseEntity<GenericResponse> resetPassword(@RequestBody PasswordResetRequest request,
            @PathVariable String token, BindingResult bindingResult) {
        return ResponseEntity.ok(authenticationMapper.resetPassword(request, token, bindingResult));
    }

    @Override
    @GetMapping(VERIFY_DEVICE_VERIFICATION)
    public ResponseEntity<AuthenticationResponse> verifyDeviceToken(@PathVariable String token,
            @RequestParam boolean trust) {
        return ResponseEntity.ok(authenticationMapper.verifyDeviceVerification(token, trust));
    }
}
