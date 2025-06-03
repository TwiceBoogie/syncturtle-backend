package dev.twiceb.userservice.controller.rest;

import dev.twiceb.common.dto.response.GenericResponse;
import dev.twiceb.userservice.controller.RegistrationControllerSwagger;
import dev.twiceb.userservice.dto.request.ProcessEmailRequest;
import dev.twiceb.userservice.dto.request.RegistrationRequest;
import dev.twiceb.userservice.dto.response.RegistrationEndResponse;
import dev.twiceb.userservice.mapper.RegistrationMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static dev.twiceb.common.constants.PathConstants.*;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping(UI_V1_AUTH)
public class RegistrationController implements RegistrationControllerSwagger {

    private final RegistrationMapper registrationMapper;

    @Override
    @PostMapping(REGISTRATION_CHECK)
    public ResponseEntity<GenericResponse> registration(RegistrationRequest request, BindingResult bindingResult) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registrationMapper.registration(request, bindingResult));
    }

    @Override
    @PostMapping(REGISTRATION_CODE)
    public ResponseEntity<GenericResponse> sendRegistrationCode(ProcessEmailRequest request,
            BindingResult bindingResult) {
        return ResponseEntity.ok(registrationMapper.sendRegistrationCode(request.getEmail(), bindingResult));
    }

    @Override
    @GetMapping(REGISTRATION_ACTIVATE_CODE)
    public ResponseEntity<GenericResponse> checkRegistrationCode(@PathVariable String code,
            HttpServletResponse response) {
        RegistrationEndResponse regResponse = registrationMapper.checkRegistrationCode(code);

        // Set the deviceToken cookie
        ResponseCookie cookie = ResponseCookie.from(AUTH_USER_DEVICE_KEY, regResponse.getDeviceToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofDays(90))
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        GenericResponse responseToClient = new GenericResponse();
        responseToClient.setMessage(regResponse.getMessage());
        return ResponseEntity.ok(responseToClient);
    }
}
