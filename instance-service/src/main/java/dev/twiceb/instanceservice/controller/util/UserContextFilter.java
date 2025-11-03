package dev.twiceb.instanceservice.controller.util;

import static dev.twiceb.common.constants.PathConstants.AUTH_USER_ID_HEADER;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import dev.twiceb.common.dto.context.AuthContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserContextFilter extends OncePerRequestFilter {

    private static final String MDC_USER_ID = "userId";

    @Override
    protected void doFilterInternal(@SuppressWarnings("null") HttpServletRequest request,
            @SuppressWarnings("null") HttpServletResponse response,
            @SuppressWarnings("null") FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String userIdHeader = request.getHeader(AUTH_USER_ID_HEADER);
            if (userIdHeader != null) {
                try {
                    UUID userId = UUID.fromString(userIdHeader);
                    AuthContext.set(userId);
                    MDC.put(MDC_USER_ID, userId.toString());
                } catch (Exception e) {
                    // bad uuid just ignore
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            AuthContext.clear();
            MDC.remove(MDC_USER_ID);
        }

    }

    @Override
    protected boolean shouldNotFilter(@SuppressWarnings("null") HttpServletRequest request)
            throws ServletException {
        String p = request.getRequestURI();
        return p.startsWith("/actuator") || p.startsWith("/health") || p.startsWith("/favicon")
                || p.startsWith("/assets/") || p.startsWith("/static/");
    }

}
