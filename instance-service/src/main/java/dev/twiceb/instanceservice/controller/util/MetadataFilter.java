package dev.twiceb.instanceservice.controller.util;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import dev.twiceb.common.dto.context.RequestMetadataContext;
import dev.twiceb.common.dto.request.RequestMetadata;
import dev.twiceb.common.dto.request.util.MetadataHeaders;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class MetadataFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@SuppressWarnings("null") HttpServletRequest request,
            @SuppressWarnings("null") HttpServletResponse response,
            @SuppressWarnings("null") FilterChain filterChain)
            throws ServletException, IOException {
        String ipAddress = request.getHeader(MetadataHeaders.FORWARDED_FOR);
        if (ipAddress != null && ipAddress.contains(",")) {
            // take first if multiple
            ipAddress = ipAddress.split(",")[0].trim();
        }
        if (ipAddress == null || ipAddress.isBlank()) {
            ipAddress = request.getRemoteAddr();
        }

        String domain = Optional.ofNullable(request.getHeader(MetadataHeaders.FORWARDED_HOST))
                .orElseGet(() -> request.getHeader(MetadataHeaders.HOST));
        String requestId = Optional.ofNullable(request.getHeader(MetadataHeaders.REQUEST_ID))
                .orElse(UUID.randomUUID().toString());
        String correlationId = Optional
                .ofNullable(request.getHeader(MetadataHeaders.CORRELATION_ID)).orElse(requestId);

        RequestMetadata metadata =
                RequestMetadata.builder().userAgent(request.getHeader(MetadataHeaders.USER_AGENT))
                        .ipAddress(ipAddress).referer(request.getHeader(MetadataHeaders.REFERER))
                        .domain(domain).requestId(requestId).correlationId(correlationId)
                        .acceptLanguage(request.getHeader(MetadataHeaders.ACCEPT_LANGUAGE))
                        .httpMethod(request.getMethod()).build();
        // attach it to the incoming request
        // must call @RequestAttribute('requestMetadata') to actually use it
        request.setAttribute("requestMetadata", metadata);
        // expose to Feign
        RequestMetadataContext.set(metadata);

        // put int MDC for logs;
        MDC.put("requestId", requestId);
        MDC.put("correlationId", correlationId);
        MDC.put("ip", ipAddress);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("requestId");
            MDC.remove("correlationId");
            MDC.remove("ip");
            RequestMetadataContext.clear();
        }
    }
}
