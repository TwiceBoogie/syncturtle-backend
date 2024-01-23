package dev.twiceb.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiRequestException extends RuntimeException {

    private HttpStatus status;

    public ApiRequestException(String msg) {
        super(msg);
    }

    public ApiRequestException(String msg, HttpStatus status) {
        super(msg);
        this.status = status;
    }
}
