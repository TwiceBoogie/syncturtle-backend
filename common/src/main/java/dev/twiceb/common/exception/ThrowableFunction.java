package dev.twiceb.common.exception;

@FunctionalInterface
public interface ThrowableFunction<T, R, E extends Exception> {
    R execute(T t) throws E;
}

