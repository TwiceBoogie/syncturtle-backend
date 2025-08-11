package dev.twiceb.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;

import static dev.twiceb.common.constants.PathConstants.*;

@Configuration
public class FeignConfiguration {

    @Bean
    RequestInterceptor requestInterceptor() {
        return template -> {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
                template.header(AUTH_USER_ID_HEADER, request.getHeader(AUTH_USER_ID_HEADER));
            }
        };
    }
}
