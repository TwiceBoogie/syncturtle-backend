package dev.twiceb.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import dev.twiceb.common.dto.context.AuthContext;
import dev.twiceb.common.dto.context.RequestMetadataContext;
import dev.twiceb.common.dto.request.RequestMetadata;
import feign.RequestInterceptor;

import static dev.twiceb.common.constants.PathConstants.*;
import java.util.UUID;

@Configuration
public class FeignConfiguration {

    @Bean
    RequestInterceptor requestInterceptor() {
        return template -> {
            UUID user = AuthContext.get();
            RequestMetadata md = RequestMetadataContext.get();

            if (md == null) {
                // fallback
                RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
                if (attrs instanceof ServletRequestAttributes sra) {
                    Object attr = sra.getRequest().getAttribute("requestMetadata");
                    if (attr instanceof RequestMetadata m) {
                        md = m;
                    }
                }
            }

            if (md != null) {
                md.toHeaders().forEach(template::header);
            }

            if (user != null)
                template.header(AUTH_USER_ID_HEADER, user.toString());
        };
    }
}
