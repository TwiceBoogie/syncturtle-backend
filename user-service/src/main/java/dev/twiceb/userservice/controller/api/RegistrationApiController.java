package dev.twiceb.userservice.controller.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dev.twiceb.common.dto.internal.AuthAdminResult;
import dev.twiceb.common.dto.request.AdminSignupRequest;
import dev.twiceb.common.dto.request.RequestMetadata;
import dev.twiceb.userservice.dto.request.AuthContextRequest;
import dev.twiceb.userservice.service.RegistrationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static dev.twiceb.common.constants.PathConstants.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(INTERNAL_V1_USER)
public class RegistrationApiController {

    private final RegistrationService registrationService;

    @PostMapping(ADMINS_SIGNUP)
    public AuthAdminResult adminSignup(@RequestBody AdminSignupRequest request,
            @RequestAttribute("requestMetadata") RequestMetadata meta) {
        log.info(request.toString());
        return registrationService
                .adminSignup(new AuthContextRequest<AdminSignupRequest>(meta, request));
    }
}
