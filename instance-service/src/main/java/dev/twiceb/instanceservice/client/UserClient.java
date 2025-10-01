package dev.twiceb.instanceservice.client;

import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import dev.twiceb.common.config.FeignConfiguration;
import dev.twiceb.common.constants.FeignConstants;
import dev.twiceb.common.constants.PathConstants;
import dev.twiceb.common.dto.internal.AuthAdminResult;
import dev.twiceb.common.dto.request.AdminSignupRequest;

@FeignClient(value = FeignConstants.USER_SERVICE, path = PathConstants.INTERNAL_V1_USER,
        configuration = FeignConfiguration.class)
public interface UserClient {

    @GetMapping(PathConstants.EMAIL_PATH_VAR)
    UUID getUserIdByEmail(@PathVariable("email") String email);

    @PostMapping(PathConstants.ADMINS_SIGNUP)
    AuthAdminResult createUser(@RequestBody AdminSignupRequest request);
}
