package dev.twiceb.apigateway.filter;

import dev.twiceb.apigateway.service.UserService;
import dev.twiceb.common.dto.response.UserPrincipleResponse;
import dev.twiceb.common.exception.JwtAuthenticationException;
import dev.twiceb.common.security.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import static dev.twiceb.common.constants.ErrorMessage.JWT_TOKEN_EXPIRED;
import static dev.twiceb.common.constants.PathConstants.*;

import java.util.List;

@Slf4j
@Component
public class AuthGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthGatewayFilterFactory.Config> {

    private final JwtProvider jwtProvider;
    private final UserService userService;

    public AuthGatewayFilterFactory(JwtProvider jwtProvider, UserService userService) {
        super(Config.class);
        this.jwtProvider = jwtProvider;
        this.userService = userService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            log.info("==> Inside Auth filter");
            String token = jwtProvider.resolveToken(exchange.getRequest());
            boolean isTokenValid = jwtProvider.validateToken(token, "main");
            if (token != null && isTokenValid) {
                String email = jwtProvider.parseToken(token);
                // store email in exchange which is only available to downstream filters.
                exchange.getAttributes().put("email", email);

                UserPrincipleResponse user = userService.getCachedUserDetails(email);

                ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
                builder.header(AUTH_USER_ID_HEADER, String.valueOf(user.getId()));

                // CSRF check (for non-GET request)
                String method = exchange.getRequest().getMethod().name();
                if (!method.equalsIgnoreCase("GET") && !method.equalsIgnoreCase("HEAD")
                        && !method.equalsIgnoreCase("OPTIONS")) {
                    String csrfCookie = null;
                    List<HttpCookie> cookies = exchange.getRequest().getCookies().get("token");
                    if (cookies != null && !cookies.isEmpty()) {
                        csrfCookie = cookies.get(0).getValue();
                    }
                    String csrfHeader = exchange.getRequest().getHeaders().getFirst("X-XSRF-TOKEN");

                    if (csrfCookie == null || csrfHeader == null || !csrfCookie.equals(csrfHeader)) {
                        throw new JwtAuthenticationException("CSRF token mismatch or missing");
                    }
                }

                return chain.filter(exchange.mutate().request(builder.build()).build());
            } else {
                log.error("failed test");
                throw new JwtAuthenticationException(JWT_TOKEN_EXPIRED);
            }
        });
    }

    public static class Config {
    }
}
