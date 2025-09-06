package dev.twiceb.userservice.controller.util;

import java.util.Optional;
import java.util.UUID;
import dev.twiceb.userservice.dto.request.MetadataDto;
import jakarta.servlet.http.HttpServletRequest;

public class RequestMetadataExtractor {

    public static MetadataDto from(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress != null && ipAddress.contains(",")) {
            // take first if multiple
            ipAddress = ipAddress.split(",")[0].trim();
        }
        if (ipAddress == null || ipAddress.isBlank()) {
            ipAddress = request.getRemoteAddr();
        }

        String domain = Optional.ofNullable(request.getHeader("X-Forwaded-Host"))
                .orElseGet(() -> request.getHeader("Host"));
        String requestId = Optional.ofNullable(request.getHeader("X-Request-Id"))
                .orElse(UUID.randomUUID().toString());
        String correlationId =
                Optional.ofNullable(request.getHeader("X-Correlation-Id")).orElse(requestId);

        return MetadataDto.builder().userAgent(request.getHeader("User-Agent")).ipAddress(ipAddress)
                .referer(request.getHeader("Referer")).domain(domain).requestId(requestId)
                .correlationId(correlationId).acceptLanguage(request.getHeader("Accept-Language"))
                .httpMethod(request.getMethod()).build();
    }
}
