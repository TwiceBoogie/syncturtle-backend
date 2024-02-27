package dev.twiceb.common.util;

import dev.twiceb.common.exception.ApiRequestException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static dev.twiceb.common.constants.ErrorMessage.USER_NOT_FOUND;
import static dev.twiceb.common.constants.PathConstants.AUTH_USER_ID_HEADER;

public class AuthUtil {

    public static Long getAuthenticatedUserId() {
        RequestAttributes attribs = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) attribs).getRequest();
        String userId = request.getHeader(AUTH_USER_ID_HEADER);

        if (userId == null) {
            throw new ApiRequestException(USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        } else {
            return Long.parseLong(userId);
        }
    }
}
