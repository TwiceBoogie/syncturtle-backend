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

    private final RedisTemplate<String, CachedHttpResponse> template;

    // https://docs.spring.io/spring-framework/reference/core/aop/ataspectj/advice.html#aop-ataspectj-around-advice
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
        log.info("CacheKey: {}", cacheKey);
        ValueOperations<String, CachedHttpResponse> ops = template.opsForValue();

        CachedHttpResponse cached = ops.get(cacheKey);
        if (cached instanceof CachedHttpResponse cachedResponse && cached != null) {
            log.info("--> returning a ResponseEntity <--");
            return ResponseEntity.status(cachedResponse.getStatus()).body(cachedResponse.getBody());
        }

        // this would keep going and call the service layer
        Object result = joinPoint.proceed();
        log.info("--> controller method called and returned <--");
        if (result instanceof ResponseEntity<?> response
                && response.getStatusCode().is2xxSuccessful()) {
            CachedHttpResponse toCache =
                    new CachedHttpResponse(response.getStatusCode().value(), response.getBody());
            ops.set(cacheKey, toCache, Duration.ofSeconds(cacheAnno.ttl()));
        }

        return result;
    }

}
