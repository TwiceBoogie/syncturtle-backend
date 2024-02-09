package dev.twiceb.userservice.feign;

import org.springframework.cloud.openfeign.FeignClient;

import static dev.twiceb.common.constants.FeignConstants.PASSWORD_SERVICE;
import static dev.twiceb.common.constants.PathConstants.API_V1_PASSWORD;

@FeignClient(name = PASSWORD_SERVICE, path = API_V1_PASSWORD, contextId = "PasswordClient")
public interface PasswordClient {
}
