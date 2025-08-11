package dev.twiceb.instanceservice.controller.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheResponse {
    String cacheName();

    String path() default "";

    long ttl() default 3600; // seconds

    boolean user() default true;
}
