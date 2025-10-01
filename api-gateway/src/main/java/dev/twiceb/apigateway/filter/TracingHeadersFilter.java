package dev.twiceb.apigateway.filter;

import java.util.UUID;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import dev.twiceb.common.dto.request.util.MetadataHeaders;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class TracingHeadersFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Inside TracingHeadersFilter");
        ServerHttpRequest request = exchange.getRequest();
        String requestId = nvl(request.getHeaders().getFirst(MetadataHeaders.REQUEST_ID),
                UUID.randomUUID().toString());
        String correlationId =
                nvl(request.getHeaders().getFirst(MetadataHeaders.CORRELATION_ID), requestId);

        String host = nvl(request.getHeaders().getFirst(MetadataHeaders.FORWARDED_HOST), nvl(
                request.getHeaders().getFirst(MetadataHeaders.HOST), request.getURI().getHost()));
        String clientIp =
                firstForwardedFor(request.getHeaders().getFirst(MetadataHeaders.FORWARDED_FOR));
        if (clientIp == null && request.getRemoteAddress() != null) {
            clientIp = request.getRemoteAddress().getAddress().getHostAddress();
        }
        String proto = nvl(request.getHeaders().getFirst("X-Forwarded-Proto"),
                request.getURI().getScheme());

        // mutate downstream request with headers
        ServerHttpRequest mutated = request.mutate().header(MetadataHeaders.REQUEST_ID, requestId)
                .header(MetadataHeaders.CORRELATION_ID, correlationId)
                .header(MetadataHeaders.FORWARDED_HOST, host).header("X-Forwarded-Proto", proto)
                .header(MetadataHeaders.FORWARDED_FOR, clientIp == null ? "" : clientIp).build();

        // also expose in the response for client-side debugging/logs
        exchange.getResponse().getHeaders().set(MetadataHeaders.REQUEST_ID, requestId);
        exchange.getResponse().getHeaders().set(MetadataHeaders.CORRELATION_ID, correlationId);

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
