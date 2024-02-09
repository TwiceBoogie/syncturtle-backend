package dev.twiceb.apigateway.filter;

import dev.twiceb.common.dto.response.UserPrincipleResponse;
import dev.twiceb.common.exception.JwtAuthenticationException;
import dev.twiceb.common.security.JwtProvider;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static dev.twiceb.common.constants.ErrorMessage.JWT_TOKEN_EXPIRED;
import static dev.twiceb.common.constants.PathConstants.*;

@Component
public class AuthGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthGatewayFilterFactory.Config> {

    private final JwtProvider jwtProvider;
    private final RestTemplate restTemplate;

    public AuthGatewayFilterFactory(JwtProvider jwtProvider, RestTemplate restTemplate) {
        super(Config.class);
        this.jwtProvider = jwtProvider;
        this.restTemplate = restTemplate;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            String token = jwtProvider.resolveToken(exchange.getRequest());
            boolean isTokenValid = jwtProvider.validateToken(token, "main");

            if (token != null && isTokenValid) {
                String email = jwtProvider.parseToken(token);
                UserPrincipleResponse user = restTemplate.getForObject(
                        String.format("http://%s:8001%s", USER_SERVICE, API_V1_AUTH + GET_USER_EMAIL),
                        UserPrincipleResponse.class,
                        email
                );

                assert user != null;
                if (!user.isVerified()) {
                    throw new JwtAuthenticationException("Email not activated");
                }
//                exchange.getRequest().mutate().header(AUTH_USER_ID_HEADER, String.valueOf(user.getId())).build();
                ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
                builder.header(AUTH_USER_ID_HEADER, String.valueOf(user.getId()));

                return chain.filter(exchange.mutate().request(builder.build()).build());
            } else {
                throw new JwtAuthenticationException(JWT_TOKEN_EXPIRED);
            }
        });
    }

    public static class Config {}
}
