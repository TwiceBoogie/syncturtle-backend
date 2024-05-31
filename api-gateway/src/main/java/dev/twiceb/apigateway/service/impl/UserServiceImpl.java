package dev.twiceb.apigateway.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import dev.twiceb.apigateway.service.UserService;
import dev.twiceb.apigateway.service.util.UserServiceHelper;
import dev.twiceb.common.dto.response.UserDeviceResponse;
import dev.twiceb.common.dto.response.UserPrincipleResponse;
import dev.twiceb.common.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;

import static dev.twiceb.common.constants.PathConstants.*;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final RestTemplate restTemplate;
    private final UserServiceHelper helper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "users", key = "#email", cacheManager = "cacheManager", unless = "#result == null")
    public UserPrincipleResponse getCachedUserDetails(String email) {
        log.info("Fetching user details not from redis");
        try {

            return Optional.ofNullable(
                            restTemplate.getForObject(
                                    String.format("http://%s:8001%s", USER_SERVICE, API_V1_AUTH + USER_EMAIL),
                                    UserPrincipleResponse.class,
                                    email))
                    .filter(UserPrincipleResponse::isVerified)
                    .orElseThrow(() -> new ApiRequestException("Email not activated", HttpStatus.BAD_REQUEST));
        } catch (RestClientException e) {
            throw new ApiRequestException("User service is currently unavailable", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getValidUserDeviceId(UserPrincipleResponse user, String deviceKey) {
        String hashedDeviceKey = helper.decodeAndHashDeviceVerificationCode(deviceKey);
        for (UserDeviceResponse userDevice : user.getUserDevices()) {
            if (hashedDeviceKey.equals(userDevice.getDeviceKey())) {
                return userDevice.getId();
            }
        }
        return null;
    }

}
