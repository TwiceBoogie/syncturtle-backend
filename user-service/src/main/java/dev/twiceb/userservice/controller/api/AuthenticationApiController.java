package dev.twiceb.userservice.controller.api;

import dev.twiceb.common.dto.response.UserPrincipleResponse;
import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.userservice.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static dev.twiceb.common.constants.PathConstants.*;

@Hidden
@RestController
@RequiredArgsConstructor
@RequestMapping(API_V1_AUTH)
public class AuthenticationApiController {

    private final AuthenticationService authenticationService;
    private final BasicMapper mapper;

    @GetMapping(USER_EMAIL)
    public UserPrincipleResponse getUserPrincipalById(@PathVariable String email) {
        return mapper.convertToResponse(
                authenticationService.getUserPrincipleByEmail(email), UserPrincipleResponse.class);
    }
}
