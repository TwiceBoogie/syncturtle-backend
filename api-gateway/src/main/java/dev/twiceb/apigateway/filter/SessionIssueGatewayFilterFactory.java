package dev.twiceb.apigateway.filter;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import dev.twiceb.apigateway.dto.SessionRecord;
import dev.twiceb.apigateway.service.SessionService;
import dev.twiceb.apigateway.service.util.SessionId;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SessionIssueGatewayFilterFactory
        extends AbstractGatewayFilterFactory<SessionIssueGatewayFilterFactory.Config> {

    private final SessionService userSession;
    private final SessionService adminSession;

    public SessionIssueGatewayFilterFactory(@Qualifier("userSession") SessionService userSession,
            @Qualifier("adminSession") SessionService adminSession) {
        super(Config.class);
        this.userSession = userSession;
        this.adminSession = adminSession;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            // PRE; we block if user already authenticated
            if (config.isBlockIfAuthenticated()) {
                // get the sessionId
                String existingId = readCookie(exchange, config.getCookieName());
                // if it exists; apply logic
                if (existingId != null) {
                    // grab sessionService based on config
                    SessionService sessionService =
                            config.getCookieName().equals("admin-session") ? adminSession
                                    : userSession;
                    Instant now = Instant.now();
                    return sessionService.find(existingId).flatMap(sess -> {
                        if (sess != null && !sess.isRevoked() && sess.getExpiresAt().isAfter(now)) {
                            // already logged in; reject
                            exchange.getResponse().setStatusCode(HttpStatus.CONFLICT);
                            return exchange.getResponse().setComplete();
                        }
                        return chain.filter(exchange);
                    }).switchIfEmpty(chain.filter(exchange));
                }
            }

            // POST
            return chain.filter(exchange).then(Mono.defer(() -> {
                ServerHttpResponse response = exchange.getResponse();

                // if an error has been thrown and caught
                if (!is2xx(response)) {
                    return Mono.empty();
                }
                // grab custom headers set by user-service
                HttpHeaders headers = response.getHeaders();
                String userId = headers.getFirst("X-Internal-UserId");
                String rolesCsv = headers.getFirst("X-Internal-Roles");

                if (userId == null) {
                    return Mono.empty();
                }

                // remove custom headers so its not exposed
                headers.remove("X-Internal-UserId");
                headers.remove("X-Internal-Roles");

                Instant now = Instant.now();
                String sessionId = SessionId.random();
                Instant expiresAt = now.plus(Duration.ofMinutes(config.sessionTtlMinutes));
                Instant absolute = now.plus(Duration.ofDays(config.absoluteTtlDays));
                List<String> roles = (rolesCsv == null || rolesCsv.isBlank()) ? List.of()
                        : List.of(rolesCsv.split(","));

                SessionRecord record = SessionRecord.builder().userId(UUID.fromString(userId))
                        .roles(roles).issuedAt(now).expiresAt(expiresAt).absoluteExpiresAt(absolute)
                        .revoked(false).build();

                SessionService sessionService =
                        config.getCookieName().equals("admin-session") ? adminSession : userSession;
                return sessionService.save(sessionId, record).then(Mono.fromRunnable(() -> {
                    ResponseCookie cookie = ResponseCookie.from(config.getCookieName(), sessionId)
                            .httpOnly(true).secure(false).sameSite("Strict").path("/")
                            .maxAge(Duration.ofMinutes(config.sessionTtlMinutes)).build();
                    response.addCookie(cookie);
                }));
            }));
        });
    }

    private static boolean is2xx(ServerHttpResponse response) {
        HttpStatusCode status = response.getStatusCode();
        return status != null && status.is2xxSuccessful();
    }

    private static String readCookie(ServerWebExchange ex, String name) {
        List<HttpCookie> cookies = ex.getRequest().getCookies().get(name);
        return (cookies == null || cookies.isEmpty()) ? null : cookies.get(0).getValue();
    }

    @Data
    public static class Config {
        private String cookieName = "__Host-session";
        private List<String> requiredRoles = List.of();
        private long sessionTtlMinutes = 30;
        private long absoluteTtlDays = 30;
        private boolean blockIfAuthenticated = false;
    }
}
