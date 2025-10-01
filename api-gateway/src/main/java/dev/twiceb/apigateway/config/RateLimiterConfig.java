package dev.twiceb.apigateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;
import java.net.InetSocketAddress;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RateLimiterConfig {

    // private final JwtProvider jwtProvider;

    // @Bean
    // @Primary // can't have 2 KeyResolver Beans which is why we need this
    // KeyResolver userKeyResolver() {
    // log.info("inside RateLimiterConfig.java");
    // return exchange -> {
    // String deviceToken = jwtProvider.resolveDeviceToken(exchange.getRequest());
    // if (deviceToken != null) {
    // String deviceKey = jwtProvider.parseDeviceToken(deviceToken);
    // return Mono.just(deviceKey);
    // } else {
    // return Mono.just(Objects.requireNonNull(exchange.getRequest().getRemoteAddress())
    // .getAddress().getHostAddress());
    // }
    // };
    // }

    @Bean
    KeyResolver ipKeyResolver() {
        log.info("inside RateLimiterConfig.java");
        return exchange -> {
            ServerHttpRequest request = exchange.getRequest();
            String ip = null;

            // if gateway sits behind a proxy/load balancer, XFF is usually set.
            // we take first ip (client) before any proxies.
            String xff = request.getHeaders().getFirst("X-Forwarded-For");
            if (xff != null && !xff.isBlank()) {
                ip = xff.split(",")[0].trim();
            }

            // nginx also set X-Real-IP
            if (ip == null || ip.isBlank()) {
                String xri = request.getHeaders().getFirst("X-Real-IP");
                if (xri != null && !xri.isBlank()) {
                    ip = xri.trim();
                }
            }

            // last resort; reactive server's remote address
            if (ip == null || ip.isBlank()) {
                InetSocketAddress ra = request.getRemoteAddress();
                if (ra != null && ra.getAddress() != null) {
                    ip = ra.getAddress().getHostAddress();
                } else {
                    ip = "unknown";
                }
            }

            // ex; request_rate_limiter.auth-refresh.<ip-address|unknown>.tokens
            // ex; request_rate_limiter.auth-refresh.<ip-address|unknown>.timestamp
            return Mono.just(ip);
        };
    }
}
