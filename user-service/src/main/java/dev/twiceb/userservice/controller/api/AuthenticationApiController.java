package dev.twiceb.userservice.controller.api;

import dev.twiceb.userservice.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static dev.twiceb.common.constants.PathConstants.*;
import java.util.UUID;

@Hidden
@RestController
@RequiredArgsConstructor
@RequestMapping(INTERNAL_V1_AUTH)
public class AuthenticationApiController {

    private final AuthenticationService authenticationService;

    @GetMapping(USER_EMAIL)
    public UUID findUserIdByEmail(@PathVariable String email) {
        return authenticationService.findUserIdByEmail(email);
    }
}
