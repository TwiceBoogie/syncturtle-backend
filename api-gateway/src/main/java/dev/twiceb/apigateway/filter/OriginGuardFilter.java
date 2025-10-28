package dev.twiceb.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class OriginGuardFilter implements GlobalFilter, Ordered {

    private static final String ALLOWED = "http://localhost";

    @Override
    public int getOrder() {
        return -99; // run early
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (!path.equals("/api/v1/auth/refresh")) {
            return chain.filter(exchange);
        }

        String origin = request.getHeaders().getFirst("Origin");
        String referer = request.getHeaders().getFirst("Referer");

        log.info("origin ==> " + origin + "referer ==> " + referer);

        boolean sameSiteOk = (origin == null && referer == null);
        boolean originOk = origin != null && origin.startsWith(ALLOWED);
        boolean refererOk = referer != null && referer.startsWith(ALLOWED);

        if (!(sameSiteOk || originOk || refererOk)) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

}
