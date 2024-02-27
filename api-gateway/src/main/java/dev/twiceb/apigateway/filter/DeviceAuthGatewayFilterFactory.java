package dev.twiceb.apigateway.filter;

import dev.twiceb.common.exception.JwtAuthenticationException;
import dev.twiceb.common.security.JwtProvider;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static dev.twiceb.common.constants.ErrorMessage.JWT_TOKEN_EXPIRED;
import static dev.twiceb.common.constants.PathConstants.AUTH_USER_DEVICE_KEY;

@Component
public class DeviceAuthGatewayFilterFactory extends AbstractGatewayFilterFactory<DeviceAuthGatewayFilterFactory.Config> {

    private final JwtProvider jwtProvider;
    private final RestTemplate restTemplate;

    public DeviceAuthGatewayFilterFactory(JwtProvider jwtProvider, RestTemplate restTemplate) {
        super(Config.class);
        this.jwtProvider = jwtProvider;
        this.restTemplate = restTemplate;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {
            String deviceToken = jwtProvider.resolveDeviceToken(exchange.getRequest());
            boolean isTokenValid = jwtProvider.validateToken(deviceToken, "deviceKey");

            if (deviceToken != null && isTokenValid) {
                String deviceKey = jwtProvider.parseDeviceToken(deviceToken);

                ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
                builder.headers(httpHeaders -> {
                    httpHeaders.remove(AUTH_USER_DEVICE_KEY); // remove existing header
                    httpHeaders.add(AUTH_USER_DEVICE_KEY, deviceKey); // replace header
                });

                return chain.filter(exchange.mutate().request(builder.build()).build());
            } else {
                // TODO: else if token is not valid check redis store to check if a refresh token is present.
                throw new JwtAuthenticationException(JWT_TOKEN_EXPIRED);
            }
        }));
    }

    public static class Config {}
}
