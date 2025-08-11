package dev.twiceb.userservice.utils;

import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
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
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress != null && ipAddress.contains(",")) {
            // take first if multiple
            ipAddress = ipAddress.split(",")[0].trim();
        }
        if (ipAddress == null || ipAddress.isBlank()) {
            ipAddress = request.getRemoteAddr();
        }

        MetadataDto metadata = MetadataDto.builder().userAgent(request.getHeader("User-Agent"))
                .ipAddress(ipAddress).referer(request.getHeader("Referer"))
                .acceptLanguage(request.getHeader("Accept-Language"))
                .httpMethod(request.getMethod()).build();
        request.setAttribute("requestMetadata", metadata);
        filterChain.doFilter(request, response);
    }



}
