package dev.twiceb.instanceservice.controller.util;

import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class CacheControlAspect {

    @After("@annotation(dev.twiceb.instanceservice.controller.util.CacheControl)")
    public Object applyCacheHeaders(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("--> inside CacheControlAspect.java <--");
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        CacheControl annotation = method.getAnnotation(CacheControl.class);

        HttpServletResponse response =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getResponse();

        if (response != null) {
            String value = annotation.privateCache() ? "private, max-age=" + annotation.maxAge()
                    : "public, max-age=" + annotation.maxAge();
            response.setHeader("Cache-Control", value);
        }

        return joinPoint.proceed();
    }
}
