package dev.twiceb.userservice.utils;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import dev.twiceb.common.dto.request.util.MetadataHeaders;
import dev.twiceb.userservice.dto.request.MetadataDto;
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

        MetadataDto metadata =
                MetadataDto.builder().userAgent(request.getHeader(MetadataHeaders.USER_AGENT))
                        .ipAddress(ipAddress).referer(request.getHeader(MetadataHeaders.REFERER))
                        .domain(domain).requestId(requestId).correlationId(correlationId)
                        .acceptLanguage(request.getHeader(MetadataHeaders.ACCEPT_LANGUAGE))
                        .httpMethod(request.getMethod()).build();
        // attach it to the incoming request
        // must call @RequestAttribute('requestMetadata') to actually use it
        request.setAttribute("requestMetadata", metadata);
        filterChain.doFilter(request, response);
    }

}
