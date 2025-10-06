package dev.twiceb.instanceservice.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import dev.twiceb.instanceservice.util.PermissionInterceptor;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final PermissionInterceptor interceptor;

    @Override
    @SuppressWarnings("null")
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/instances/**")
                .excludePathPatterns("/instances/admins/sign-up");
    }
}
