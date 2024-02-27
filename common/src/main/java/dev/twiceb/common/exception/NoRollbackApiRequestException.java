package dev.twiceb.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NoRollbackApiRequestException extends RuntimeException {

    private HttpStatus status;

    public NoRollbackApiRequestException(String msg) {super(msg);}

    public NoRollbackApiRequestException(String msg, HttpStatus status) {
        super(msg);
        this.status = status;
    }
}
