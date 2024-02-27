package dev.twiceb.userservice.controller.rest;

import dev.twiceb.common.dto.response.GenericResponse;
import dev.twiceb.common.exception.NoRollbackApiRequestException;
import dev.twiceb.userservice.dto.request.*;
import dev.twiceb.userservice.dto.response.AuthenticationResponse;
import dev.twiceb.userservice.mapper.AuthenticationMapper;
import dev.twiceb.userservice.model.PasswordResetOtp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static dev.twiceb.common.constants.PathConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(UI_V1_AUTH)
public class AuthenticationController {

    private final AuthenticationMapper authenticationMapper;

    @PostMapping(LOGIN)
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody AuthenticationRequest request,
            BindingResult bindingResult
    ) {
        return ResponseEntity.ok(authenticationMapper.login(request, bindingResult));
    }

    @PostMapping(FORGOT_USERNAME)
    public ResponseEntity<GenericResponse> forgotUsername(
            @Valid @RequestBody ProcessEmailRequest request,
            BindingResult bindingResult
    ) {
        return ResponseEntity.ok(authenticationMapper.forgotUsername(request.getEmail(), bindingResult));
    }

    @PostMapping(FORGOT_PASSWORD)
    public ResponseEntity<GenericResponse> forgotPassword(
            @Valid @RequestBody ProcessEmailRequest request,
            BindingResult bindingResult
            ) {
        return ResponseEntity.ok(authenticationMapper.forgotPassword(request.getEmail(), bindingResult));
    }

    @PostMapping(VERIFY_OTP)
    public ResponseEntity<GenericResponse> verifyOtp(
            @Valid @RequestBody PasswordOtpRequest request,
            BindingResult bindingResult
            ) {
        return ResponseEntity.ok(authenticationMapper.verifyOtp(request.getOtp(), bindingResult));
    }

    @PostMapping(RESET)
    public ResponseEntity<GenericResponse> resetPassword(
            @Valid @RequestBody PasswordResetRequest request,
            @PathVariable("token") String token,
            BindingResult bindingResult
            ) {
        return ResponseEntity.ok(authenticationMapper.resetPassword(request, token, bindingResult));
    }

    @GetMapping(VERIFY_DEVICE_VERIFICATION)
    public ResponseEntity<AuthenticationResponse> verifyDeviceToken(
            @PathVariable("token") String token,
            @RequestParam("trust") boolean trust
    ) {
        return ResponseEntity.ok(authenticationMapper.verifyDeviceVerification(token, trust));
    }

    @GetMapping("/throw")
    public void throwException() {
        throw new NoRollbackApiRequestException("Test NoRollBackException", HttpStatus.BAD_REQUEST);
    }
}
