package dev.twiceb.apigateway.filter;

import java.util.Optional;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import dev.twiceb.apigateway.service.util.HmacCsrfToken;
import dev.twiceb.common.exception.ApiRequestException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CsrfProjectionFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        return -50;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        // String path = request.getPath().value();
        HttpMethod method = request.getMethod();

        // boolean protectedPath = path.equals("/api/v1/auth/refresh");
        boolean unsafe = HttpMethod.POST.equals(method) || HttpMethod.PUT.equals(method)
                || HttpMethod.PATCH.equals(method) || HttpMethod.DELETE.equals(method);
        log.info("Inside csrfProjectionFilter");
        if (!unsafe) {
            return chain.filter(exchange);
        }
        log.info("Inside still csrfProj");
        Optional<HttpCookie> optionalCookie =
                Optional.ofNullable(request.getCookies().getFirst("csrftoken"));
        String cookieValue = optionalCookie.isPresent() ? optionalCookie.get().getValue() : null;
        String headerValue = request.getHeaders().getFirst("X-CSRF-TOKEN");
        log.info("cookieValue: {}, headerValue: {}", cookieValue, headerValue);
        if (cookieValue == null || headerValue == null) {
            throw new ApiRequestException("CSRF_TOKEN_NOT_FOUND", HttpStatus.FORBIDDEN);
        }

        if (!HmacCsrfToken.constantTimeEquals(cookieValue, headerValue)
                && !HmacCsrfToken.isValid(headerValue)) {
            throw new ApiRequestException("INVALID_CSRF_TOKEN", HttpStatus.FORBIDDEN);
        }

        return chain.filter(exchange);

    }

}
