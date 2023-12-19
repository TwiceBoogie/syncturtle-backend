package dev.twiceb.common.exception;

import dev.twiceb.common.dto.response.ApiErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InputFieldException.class)
    public ResponseEntity<ApiErrorResponse> handleInputFieldException(InputFieldException exception) {
        InputFieldException inputFieldException;
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();

        if (exception.getBindingResult() != null) {
            inputFieldException = new InputFieldException(exception.getBindingResult());
        } else {
            inputFieldException = new InputFieldException(exception.getStatus(), exception.getErrorsMap());
        }

        apiErrorResponse.setMessage("Input Field Exception");
        apiErrorResponse.setErrors(exception.getErrorsMap());

        return ResponseEntity.status(inputFieldException.getStatus()).body(apiErrorResponse);
    }

    @ExceptionHandler(ApiRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleApiRequestException(ApiRequestException exception) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setMessage(exception.getMessage());
        apiErrorResponse.setErrors(null);

        return ResponseEntity.status(exception.getStatus()).body(apiErrorResponse);
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleJWTRequestException(JwtAuthenticationException exception) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setMessage(exception.getMessage());
        apiErrorResponse.setErrors(null);

        return ResponseEntity.status(exception.getHttpStatus()).body(apiErrorResponse);
    }


}
