package dev.twiceb.common.util;

import static dev.twiceb.common.constants.PathConstants.AUTH_USER_ID_HEADER;
import java.io.IOException;
import java.util.UUID;
import org.springframework.stereotype.Component;
import dev.twiceb.common.dto.context.AuthContext;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ContextFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        try {
            String user = req.getHeader(AUTH_USER_ID_HEADER);
            if (user != null)
                AuthContext.set(UUID.fromString(user));
            chain.doFilter(request, response);
        } finally {
            // always clear
            AuthContext.clear();
        }
    }

}
