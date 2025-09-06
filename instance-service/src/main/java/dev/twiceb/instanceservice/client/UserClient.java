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
import dev.twiceb.common.dto.request.AdminSignupRequest;
import dev.twiceb.common.dto.response.AdminTokenGrant;

@FeignClient(value = FeignConstants.USER_SERVICE, path = PathConstants.API_V1_AUTH,
        configuration = FeignConfiguration.class)
public interface UserClient {

    @GetMapping("/user/{email}")
    UUID getUserIdByEmail(@PathVariable("email") String email);

    @PostMapping("/user/admin")
    AdminTokenGrant createUser(@RequestBody AdminSignupRequest request);
}
