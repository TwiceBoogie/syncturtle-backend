package dev.twiceb.apigateway.config;

import dev.twiceb.common.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Configuration
@RequiredArgsConstructor
public class RateLimiterConfig {

    private final JwtProvider jwtProvider;

    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> {
            String deviceToken = jwtProvider.resolveDeviceToken(exchange.getRequest());
            if (deviceToken != null) {
                String deviceKey = jwtProvider.parseDeviceToken(deviceToken);
                return Mono.just(deviceKey);
            } else {
                return Mono.just(
                        Objects.requireNonNull(exchange.getRequest()
                                        .getRemoteAddress())
                                .getAddress()
                                .getHostAddress());
            }
        };
    }
}
