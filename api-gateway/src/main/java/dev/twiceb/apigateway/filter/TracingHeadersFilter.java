package dev.twiceb.apigateway.filter;

import static dev.twiceb.common.util.StringHelper.firstNonBlank;

import java.net.InetSocketAddress;
import java.util.Optional;
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
        ServerHttpRequest request = exchange.getRequest();

        String requestId = firstNonBlank(request.getHeaders().getFirst(MetadataHeaders.REQUEST_ID),
                UUID.randomUUID().toString());

        String correlationId = firstNonBlank(
                request.getHeaders().getFirst(MetadataHeaders.CORRELATION_ID), requestId);

        String host = firstNonBlank(request.getHeaders().getFirst(MetadataHeaders.FORWARDED_HOST),
                request.getHeaders().getFirst(MetadataHeaders.HOST), request.getURI().getHost(),
                "localhost");

        String clientIp = firstNonBlank(
                firstForwardedFor(request.getHeaders().getFirst(MetadataHeaders.FORWARDED_FOR)),
                Optional.ofNullable(request.getRemoteAddress()).map(InetSocketAddress::getAddress)
                        .map(addr -> addr.getHostAddress()).orElse(null),
                "");

        String proto = firstNonBlank(request.getHeaders().getFirst("X-Forwarded-Proto"),
                request.getURI().getScheme(), "http");

        // mutate downstream request with headers
        ServerHttpRequest mutated = request.mutate().header(MetadataHeaders.REQUEST_ID, requestId)
                .header(MetadataHeaders.CORRELATION_ID, correlationId)
                .header(MetadataHeaders.FORWARDED_HOST, host).header("X-Forwarded-Proto", proto)
                .header(MetadataHeaders.FORWARDED_FOR, clientIp).build();

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

}
