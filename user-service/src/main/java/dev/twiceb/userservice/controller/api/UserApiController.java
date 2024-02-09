package dev.twiceb.userservice.controller.api;

import dev.twiceb.userservice.service.UserClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static dev.twiceb.common.constants.PathConstants.*;

@RestController
@RequestMapping(API_V1_USER)
@RequiredArgsConstructor
public class UserApiController {

    private final UserClientService userService;

    @GetMapping(GET_USER_EMAIL)
    public String getUserEmail(@PathVariable("userId") Long userId) {
        return userService.getUserEmail(userId);
    }

    @GetMapping(ADD_NOTIFICATION)
    public void increaseNotificationCount(@PathVariable("userId") Long userId) {
        userService.increaseNotificationCount(userId);
    }

    @GetMapping(SUB_NOTIFICATION)
    public void decreaseNotificationCount(@PathVariable("userId") Long userId) {
        userService.decreaseNotificationCount(userId);
    }

    @GetMapping(NOTIFICATION_USER_ID)
    public void resetNotificationCount(@PathVariable("userId") Long userId) {
        userService.resetNotificationCount(userId);
    }
}
