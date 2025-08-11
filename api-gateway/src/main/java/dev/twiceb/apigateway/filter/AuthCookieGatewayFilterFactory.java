package dev.twiceb.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthCookieGatewayFilterFactory
        extends AbstractGatewayFilterFactory<AuthCookieGatewayFilterFactory.Config> {

    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'apply'");
    }

}
