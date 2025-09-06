package dev.twiceb.apigateway.filter;

import java.util.UUID;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class TracingHeadersFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestId =
                nvl(request.getHeaders().getFirst("X-Request-Id"), UUID.randomUUID().toString());
        String correlationId = nvl(request.getHeaders().getFirst("X-Correlation-Id"), requestId);

        String host = nvl(request.getHeaders().getFirst("X-Forwarded-Host"),
                nvl(request.getHeaders().getFirst("Host"), request.getURI().getHost()));
        String clientIp = firstForwardedFor(request.getHeaders().getFirst("X-Forwarded-For"));
        if (clientIp == null && request.getRemoteAddress() != null) {
            clientIp = request.getRemoteAddress().getAddress().getHostAddress();
        }
        String proto = nvl(request.getHeaders().getFirst("X-Forwarded-Proto"),
                request.getURI().getScheme());

        // mutate downstream request with headers
        ServerHttpRequest mutated = request.mutate().header("X-Request_id", requestId)
                .header("X-Correlation-Id", correlationId).header("X-Forwarded-Host", host)
                .header("X-Forwarded-Proto", proto)
                .header("X-Forwarded-For", clientIp == null ? "" : clientIp).build();

        // also expose in the response for client-side debugging/logs
        exchange.getResponse().getHeaders().set("X-Request-Id", requestId);
        exchange.getResponse().getHeaders().set("X-Correlation-Id", correlationId);

        return chain.filter(exchange.mutate().request(mutated).build());

    }

    private String firstForwardedFor(String xff) {
        if (xff == null || xff.isBlank())
            return null;
        int comma = xff.indexOf(",");
        return comma >= 0 ? xff.substring(0, comma).trim() : xff.trim();
    }

    private static String nvl(String val, String def) {
        if (val == null || val.isBlank()) {
            return def;
        }
        return val;
    }

}
