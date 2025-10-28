package dev.twiceb.apigateway.filter;

import java.time.Duration;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import dev.twiceb.apigateway.service.SessionService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SessionLogoutAuthGatewayFilterFactory
        extends AbstractGatewayFilterFactory<SessionLogoutAuthGatewayFilterFactory.Config> {

    private final SessionService userSession;
    private final SessionService adminSession;

    public SessionLogoutAuthGatewayFilterFactory(
            @Qualifier("userSession") SessionService userSession,
            @Qualifier("adminSession") SessionService adminSession) {
        super(Config.class);
        this.userSession = userSession;
        this.adminSession = adminSession;
    }

    @Data
    public static class Config {
        private String cookieName = "__Host-session";
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            return chain.filter(exchange).then(Mono.defer(() -> {
                ServerHttpResponse response = exchange.getResponse();
                if (!is2xx(response)) {
                    return Mono.empty();
                }

                String sessionId = readCookie(exchange, config.getCookieName());
                if (sessionId == null) {
                    return Mono.empty();
                }
                SessionService sessionService =
                        config.getCookieName().equals("__Host-session") ? adminSession
                                : userSession;
                return sessionService.delete(sessionId).then(Mono.fromRunnable(() -> {
                    ResponseCookie cookie = ResponseCookie.from(config.getCookieName(), "delete")
                            .httpOnly(true).secure(false).sameSite("Strict").path("/")
                            .maxAge(Duration.ZERO).build();
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

}
