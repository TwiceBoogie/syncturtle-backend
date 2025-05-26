package dev.twiceb.apigateway.filter;

import dev.twiceb.apigateway.service.UserService;
import dev.twiceb.common.exception.JwtAuthenticationException;
import dev.twiceb.common.security.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import static dev.twiceb.common.constants.ErrorMessage.JWT_TOKEN_EXPIRED;
import static dev.twiceb.common.constants.PathConstants.AUTH_DEVICE_KEY_ID;
import static dev.twiceb.common.constants.PathConstants.AUTH_USER_DEVICE_KEY;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class DeviceAuthGatewayFilterFactory
        extends AbstractGatewayFilterFactory<DeviceAuthGatewayFilterFactory.Config> {

    private final JwtProvider jwtProvider;
    private final UserService userService;

    public DeviceAuthGatewayFilterFactory(JwtProvider jwtProvider, UserService userService) {
        super(Config.class);
        this.jwtProvider = jwtProvider;
        this.userService = userService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            String deviceToken = jwtProvider.resolveDeviceToken(exchange.getRequest());
            log.info("Inside the Device auth filter");

            if (path.endsWith("/login")) {
                return handleLoginPath(exchange, chain, deviceToken);
            } else {
                return handleOtherPaths(exchange, chain, deviceToken);
            }
        }));
    }

    private Mono<Void> handleLoginPath(ServerWebExchange exchange, GatewayFilterChain chain, String deviceToken) {
        ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate();
        if (deviceToken != null) {
            String deviceKey = jwtProvider.parseDeviceToken(deviceToken);
            // send the base64 encoded device token as a header
            requestBuilder.headers(httpHeaders -> {
                httpHeaders.remove(AUTH_USER_DEVICE_KEY);
                httpHeaders.add(AUTH_USER_DEVICE_KEY, deviceKey);
            });
        } else {
            requestBuilder.headers(httpHeaders -> {
                httpHeaders.remove(AUTH_USER_DEVICE_KEY);
                httpHeaders.add(AUTH_USER_DEVICE_KEY, "");
            });
        }
        return chain.filter(exchange.mutate().request(requestBuilder.build()).build());
    }

    private Mono<Void> handleOtherPaths(ServerWebExchange exchange, GatewayFilterChain chain, String deviceToken) {
        if (deviceToken != null && jwtProvider.validateToken(deviceToken, "deviceKey")) {
            String deviceKey = jwtProvider.parseDeviceToken(deviceToken);
            String email = exchange.getAttribute("email");

            return Optional.ofNullable(email)
                    .map(userService::getCachedUserDetails)
                    .map(user -> userService.getValidUserDeviceId(user, deviceKey))
                    .map(deviceId -> addHeadersAndContinue(exchange, chain, deviceId))
                    .orElseThrow(() -> new JwtAuthenticationException(JWT_TOKEN_EXPIRED));
        } else {
            throw new JwtAuthenticationException(JWT_TOKEN_EXPIRED);
        }
    }

    private Mono<Void> addHeadersAndContinue(ServerWebExchange exchange, GatewayFilterChain chain, UUID deviceId) {
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .headers(httpHeaders -> {
                    httpHeaders.remove(AUTH_USER_DEVICE_KEY);
                    httpHeaders.add(AUTH_DEVICE_KEY_ID, String.valueOf(deviceId));
                })
                .build();
        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    public static class Config {
    }
}
