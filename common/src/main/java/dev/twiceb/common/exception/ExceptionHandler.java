package dev.twiceb.common.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;

public class ExceptionHandler {

    public static <T, R, E extends Exception> R handleException(ThrowableFunction<T, R, E> function, T parameter) throws Exception {
        try {
            return function.execute(parameter);
        } catch (Exception error) {
            if (error instanceof DataAccessException) {
                throw new ApiRequestException("Internal error occured, please try again later", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            throw new Exception("something wrong");
        }
    }
    
}
