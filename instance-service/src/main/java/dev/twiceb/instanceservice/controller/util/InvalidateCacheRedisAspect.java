package dev.twiceb.instanceservice.controller.util;

import static dev.twiceb.common.constants.PathConstants.AUTH_USER_ID_HEADER;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;
import dev.twiceb.instanceservice.dto.internal.CachedHttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class InvalidateCacheRedisAspect {

    private final RedisTemplate<String, CachedHttpResponse> template;

    @Around("@annotation(invalidateAnno)")
    public Object around(ProceedingJoinPoint joinPoint, InvalidateCacheRedis invalidateAnno)
            throws Throwable {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();
        String userId = "anonymous";

        if (invalidateAnno.user()) {
            String header = request.getHeader(AUTH_USER_ID_HEADER);
            if (header != null) {
                try {
                    userId = UUID.fromString(header).toString();
                } catch (Exception e) {
                    log.warn("Invalid UUID in X-Auth-User-Id: {}", header);
                }
            }
        }

        String path =
                !invalidateAnno.path().isEmpty() ? invalidateAnno.path() : request.getRequestURI();
        String normalizedPath = path.startsWith("/") ? path.substring(1) : path;

        if (invalidateAnno.urlParams()) {
            @SuppressWarnings("unchecked")
            Map<String, String> pathVars = (Map<String, String>) request
                    .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            for (Map.Entry<String, String> entry : pathVars.entrySet()) {
                normalizedPath =
                        normalizedPath.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

        String cacheKey =
                invalidateAnno.cacheName() + ":" + userId + ":" + normalizedPath.replace("/", "_");

        if (invalidateAnno.multiple()) {
            String pattern = invalidateAnno.cacheName() + ":" + userId + ":*";
            Set<String> keys = template.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                template.delete(keys);
                log.debug("Invalidated multiple keys: {}", keys);
            }
        } else {
            template.delete(cacheKey);
            log.debug("Invalidated cache key: {}", cacheKey);
        }

        return joinPoint.proceed();
    }
}
