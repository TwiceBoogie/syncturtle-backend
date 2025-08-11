package dev.twiceb.instanceservice.controller.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InvalidateCacheRedis {
    String cacheName();

    String path() default ""; // Optional override of request path

    boolean urlParams() default false;

    boolean user() default true;

    boolean multiple() default false; // if true/ dlete all matching keys with wildcard
}
