package dev.twiceb.notificationservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static dev.twiceb.common.constants.FeignConstants.USER_SERVICE;
import static dev.twiceb.common.constants.PathConstants.*;

@FeignClient(name = USER_SERVICE, path = API_V1_USER, contextId = "UserClient")
public interface UserClient {

    @GetMapping(ADD_NOTIFICATION)
    void increaseNotificationCount(@PathVariable("userId") Long userId);

    @GetMapping(SUB_NOTIFICATION)
    void decreaseNotificationCount(@PathVariable("userId") Long userId);

    @GetMapping(NOTIFICATION_USER_ID)
    void resetNotificationCount(@PathVariable("userId") Long userId);
}
