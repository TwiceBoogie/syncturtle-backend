package dev.twiceb.instanceservice.util;

import java.util.Map;
import java.util.UUID;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import dev.twiceb.common.dto.context.AuthContext;
import dev.twiceb.instanceservice.service.Permission;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

    private final ApplicationContext beans;

    @Override
    @SuppressWarnings("null")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod hm)) {
            return false;
        }

        PermissionClasses ann = hm.getMethodAnnotation(PermissionClasses.class);
        if (ann == null) {
            ann = hm.getBeanType().getAnnotation(PermissionClasses.class);
        }
        // no pipeline
        if (ann == null) {
            return true;
        }

        UUID userId = AuthContext.get();
        log.info("UserId: {}", userId);

        @SuppressWarnings("unchecked")
        Map<String, String> pathVars = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        for (Class<? extends Permission> pClass : ann.value()) {
            Permission p = beans.getBean(pClass);
            if (!p.hasPermission(request, userId, pathVars)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "You don't have the required permissions.");
            }
        }

        return true;
    }
}
