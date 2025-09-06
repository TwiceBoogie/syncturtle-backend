package dev.twiceb.instanceservice.controller.util;

import static dev.twiceb.common.constants.PathConstants.AUTH_USER_ID_HEADER;
import java.time.Duration;
import java.util.UUID;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import dev.twiceb.instanceservice.dto.internal.CachedHttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheResponseAspect {

    private final RedisTemplate<String, Object> template;

    @Around("@annotation(cacheAnno)")
    public Object around(ProceedingJoinPoint joinPoint, CacheResponse cacheAnno) throws Throwable {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();
        String userId = "anonymous";

        if (cacheAnno.user()) {
            String header = request.getHeader(AUTH_USER_ID_HEADER);
            if (header != null) {
                try {
                    userId = UUID.fromString(header).toString();
                } catch (Exception e) {
                    log.warn("Invalid UUID in X-Auth-User-Id: {}", header);
                }
            }
        }

        String path = !cacheAnno.path().isEmpty() ? cacheAnno.path() : request.getRequestURI();
        String normalizedPath = path.startsWith("/") ? path.substring(1) : path;
        String cacheKey =
                cacheAnno.cacheName() + ":" + userId + ":" + normalizedPath.replace("/", "_");
        ValueOperations<String, Object> ops = template.opsForValue();

        Object cached = ops.get(cacheKey);
        if (cached instanceof CachedHttpResponse cachedResponse) {
            return ResponseEntity.status(cachedResponse.getStatus()).body(cachedResponse.getBody());
        }

        Object result = joinPoint.proceed();

        if (result instanceof ResponseEntity<?> response
                && response.getStatusCode().is2xxSuccessful()) {
            CachedHttpResponse toCache =
                    new CachedHttpResponse(response.getStatusCode().value(), response.getBody());
            ops.set(cacheKey, toCache, Duration.ofSeconds(cacheAnno.ttl()));
        }

        return result;
    }

}
