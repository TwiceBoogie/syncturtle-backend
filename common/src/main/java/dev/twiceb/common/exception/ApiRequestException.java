package dev.twiceb.common.exception;

import lombok.Getter;
import java.util.Map;
import org.springframework.http.HttpStatus;

@Getter
public class ApiRequestException extends RuntimeException {

    private HttpStatus status = HttpStatus.BAD_REQUEST;
    private Map<String, Object> payload;

    public ApiRequestException(String msg) {
        super(msg);
    }

    public ApiRequestException(String msg, HttpStatus status) {
        super(msg);
        this.status = status;
    }

    public ApiRequestException(String message, HttpStatus status, Map<String, Object> payload) {
        super(message);
        this.status = status;
        this.payload = payload;
    }
}
