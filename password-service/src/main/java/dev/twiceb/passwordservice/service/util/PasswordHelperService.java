package dev.twiceb.passwordsservice.service.util;

import static dev.twiceb.common.constants.ErrorMessage.PASSWORDS_NOT_MATCH;
import static dev.twiceb.common.constants.ErrorMessage.PASSWORD_LENGTH_ERROR;

import org.springframework.http.HttpStatus;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.util.ServiceHelper;
import org.springframework.stereotype.Component;

@Component
public class PasswordHelperService extends ServiceHelper {

    public void processPassword(String password1, String password2) {
        if (!password1.equals(password2)) {
            throw new ApiRequestException(PASSWORDS_NOT_MATCH, HttpStatus.BAD_REQUEST);
        }

        if (password1.length() < 9) {
            throw new ApiRequestException(PASSWORD_LENGTH_ERROR, HttpStatus.BAD_REQUEST);
        }
    }
}
