package dev.twiceb.passwordservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static dev.twiceb.common.constants.FeignConstants.USER_SERVICE;
import static dev.twiceb.common.constants.PathConstants.*;

@FeignClient(name = USER_SERVICE, path = API_V1_USER, contextId = "UserClient")
public interface UserClient {

    @GetMapping(GET_USER_EMAIL)
    String getUserEmail(@PathVariable("userId") Long userId);
}
