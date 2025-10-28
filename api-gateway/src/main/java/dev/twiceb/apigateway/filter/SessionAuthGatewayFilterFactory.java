package dev.twiceb.apigateway.filter;

import static dev.twiceb.common.constants.PathConstants.AUTH_USER_ID_HEADER;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import dev.twiceb.apigateway.service.SessionService;
import dev.twiceb.common.enums.AuthErrorCodes;
import dev.twiceb.common.exception.AuthException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SessionAuthGatewayFilterFactory
        extends AbstractGatewayFilterFactory<SessionAuthGatewayFilterFactory.Config> {

    private static final Duration SESSION_TTL = Duration.ofMinutes(30);
    private static final Duration RENEW_THRESHOLD = Duration.ofMinutes(5);

    private final SessionService userSession;
    private final SessionService adminSession;

    public SessionAuthGatewayFilterFactory(@Qualifier("userSession") SessionService userSession,
            @Qualifier("adminSession") SessionService adminSession) {
        super(Config.class);
        this.userSession = userSession;
        this.adminSession = adminSession;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // pre
            log.info("Inside SessionAuthGateway");

            Pick pick = pickCookie(exchange, config.getCookieNames());
            if (pick == null) {
                log.info("pick: {}", pick);
                return Mono.error(new AuthException(AuthErrorCodes.AUTHENTICATION_FAILED));
            }
            log.info("pick: {}", pick);
            Instant now = Instant.now();
            return pick.getService().find(pick.getSessionId())
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED)))
                    .flatMap(sess -> {
                        // 1: check if revoked or expired
                        if (sess.isRevoked() || sess.getExpiresAt().isBefore(now)) {
                            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                        }

                        // 2: role gate check
                        if (!config.getRequiredRoles().isEmpty() && sess.getRoles().stream()
                                .noneMatch(config.getRequiredRoles()::contains)) {
                            return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
                        }

                        boolean needsRenew = Duration.between(now, sess.getExpiresAt())
                                .compareTo(RENEW_THRESHOLD) < 0;

                        Mono<Void> maybeRenew = needsRenew ? pick.getService()
                                .extendExpiry(pick.getSessionId(), now.plus(SESSION_TTL))
                                .then(Mono.fromRunnable(() -> {
                                    ResponseCookie cookie = ResponseCookie
                                            .from(pick.getCookieName(), pick.getSessionId())
                                            .httpOnly(true).secure(false).sameSite("Strict")
                                            .path("/").maxAge(SESSION_TTL).build();
                                    exchange.getResponse().addCookie(cookie);
                                })) : Mono.empty();

                        // inject idenity for downstreams
                        ServerHttpRequest mutated = exchange.getRequest().mutate()
                                .header(AUTH_USER_ID_HEADER, sess.getUserId().toString())
                                .header("X-Auth-Roles", String.join(",", sess.getRoles())).build();

                        // optional internal jwt
                        if (config.isAddInternalJwt()) {

                        }

                        return maybeRenew
                                .then(chain.filter(exchange.mutate().request(mutated).build()));
                    });
        };
    }

    @Data
    public static class Config {
        private List<String> cookieNames = List.of("session"); // or "__Host-admin"
        private List<String> requiredRoles = List.of();
        private boolean addInternalJwt = false;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    private static class Pick {
        private final String cookieName;
        private final String sessionId;
        private final SessionService service;
    }

    private Pick pickCookie(ServerWebExchange ex, List<String> cookieNames) {
        for (String name : cookieNames) {
            String id = readCookie(ex, name);
            if (id != null) {
                SessionService service = "admin-session".equals(name) ? adminSession : userSession;
                return new Pick(name, id, service);
            }
        }
        return null;
    }

    private static String readCookie(ServerWebExchange ex, String name) {
        List<HttpCookie> cookies = ex.getRequest().getCookies().get(name);
        return (cookies == null || cookies.isEmpty()) ? null : cookies.get(0).getValue();
    }
}
