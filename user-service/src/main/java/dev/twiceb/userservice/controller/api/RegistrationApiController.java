package dev.twiceb.userservice.controller.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dev.twiceb.common.dto.request.AdminSignupRequest;
import dev.twiceb.common.dto.request.RequestMetadata;
import dev.twiceb.common.dto.response.AdminTokenGrant;
import dev.twiceb.userservice.dto.request.AuthContextRequest;
import dev.twiceb.userservice.service.RegistrationService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

import static dev.twiceb.common.constants.PathConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_V1_AUTH)
public class RegistrationApiController {

    private final RegistrationService registrationService;

    @PostMapping("/admins/sign-up")
    public AdminTokenGrant adminSignup(@RequestBody AdminSignupRequest request,
            @RequestAttribute("requestMetadata") RequestMetadata meta) {
        return registrationService
                .adminSignup(new AuthContextRequest<AdminSignupRequest>(meta, request));
    }
}
