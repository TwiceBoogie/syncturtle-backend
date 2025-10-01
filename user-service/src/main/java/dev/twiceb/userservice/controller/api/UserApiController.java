package dev.twiceb.userservice.controller.api;

import dev.twiceb.common.dto.response.UserPrincipalResponse;
import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.userservice.service.UserClientService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static dev.twiceb.common.constants.PathConstants.*;


@Hidden
@RestController
@RequestMapping(INTERNAL_V1_USER)
@RequiredArgsConstructor
public class UserApiController {

    private final UserClientService userService;
    private final BasicMapper mapper;

    @GetMapping(ADD_NOTIFICATION)
    public void increaseNotificationCount(@PathVariable UUID userId) {
        userService.increaseNotificationCount(userId);
    }

    @GetMapping(SUB_NOTIFICATION)
    public void decreaseNotificationCount(@PathVariable UUID userId) {
        userService.decreaseNotificationCount(userId);
    }

    @GetMapping(NOTIFICATION_USER_ID)
    public void resetNotificationCount(@PathVariable UUID userId) {
        userService.resetNotificationCount(userId);
    }

    @GetMapping(GET_USER_PRINCIPAL)
    public UserPrincipalResponse getUserPrincipal(@PathVariable("id") UUID userId) {
        return mapper.convertToResponse(userService.getUserPrincipal(userId),
                UserPrincipalResponse.class);
    }

    @GetMapping(EMAIL_PATH_VAR)
    public UUID getUserIdByEmail(@PathVariable String email) {
        return userService.findUserIdByEmail(email);
    }

}
