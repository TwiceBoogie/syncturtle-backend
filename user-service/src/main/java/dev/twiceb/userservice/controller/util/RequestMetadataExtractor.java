package dev.twiceb.userservice.controller.util;

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

        return MetadataDto.builder().userAgent(request.getHeader("User-Agent")).ipAddress(ipAddress)
                .referer(request.getHeader("Referer"))
                .acceptLanguage(request.getHeader("Accept-Language"))
                .httpMethod(request.getMethod()).build();
    }
}
