package dev.twiceb.apigateway.service.impl;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import dev.twiceb.apigateway.service.UserService;
import dev.twiceb.common.dto.response.UserPrincipalResponse;
import dev.twiceb.common.enums.AuthErrorCodes;
import dev.twiceb.common.exception.AuthException;
import lombok.RequiredArgsConstructor;

import static dev.twiceb.common.constants.PathConstants.*;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Duration TTL = Duration.ofMinutes(5);

    private final WebClient.Builder web;
    private final ReactiveRedisTemplate<String, UserPrincipalResponse> redis;
    // private final UserServiceHelper helper;

    @Override
    public Mono<UserPrincipalResponse> getCachedUserDetails(UUID userId) {
        String key = "users:" + userId;
        return redis.opsForValue().get(key)
                .switchIfEmpty(web.baseUrl("http://" + USER_SERVICE + ":8001").build().get()
                        .uri(INTERNAL_V1_USER + "/{id}/principal", userId).retrieve()
                        .bodyToMono(UserPrincipalResponse.class)
                        .filter(UserPrincipalResponse::isVerified)
                        .switchIfEmpty(
                                Mono.error(new AuthException(AuthErrorCodes.USER_DOES_NOT_EXIST)))
                        .flatMap(u -> redis.opsForValue().set(key, u, TTL).thenReturn(u)));
    }

    @Override
    public Mono<UUID> getValidUserDeviceId(UserPrincipalResponse user, String deviceKey) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getValidUserDeviceId'");
    }

    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // @Cacheable(value = "users", key = "#userId", cacheManager = "cacheManager",
    // unless = "#result == null")
    // public UserPrincipalResponse getCachedUserDetails(UUID userId) {
    // log.info(">>> SHOULD NOT REACH HERE IF CACHED <<<");
    // try {
    // return Optional
    // .ofNullable(restTemplate.getForObject(
    // String.format("http://%s:8001%s", USER_SERVICE,
    // API_V1_AUTH + "/user/token/{id}"),
    // UserPrincipalResponse.class, userId))
    // .filter(UserPrincipalResponse::isVerified)
    // .orElseThrow(() -> new ApiRequestException("Email not activated",
    // HttpStatus.BAD_REQUEST));
    // } catch (RestClientException e) {
    // throw new ApiRequestException("User service is currently unavailable",
    // HttpStatus.SERVICE_UNAVAILABLE);
    // }
    // }

    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public UUID getValidUserDeviceId(UserPrincipalResponse user, String deviceKey) {
    // String hashedDeviceKey = helper.decodeAndHashDeviceVerificationCode(deviceKey);
    // for (UserDeviceResponse userDevice : user.getUserDevices()) {
    // if (hashedDeviceKey.equals(userDevice.getDeviceKey())) {
    // return userDevice.getId();
    // }
    // }
    // return null;
    // }

}
