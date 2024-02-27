package dev.twiceb.userservice.dto.request;

import lombok.Data;

@Data
public class AuthenticationCodeRequest {
    private String activationCode;
    private String userAgent;
    private String screenWidth;
    private String screenHeight;
    private String timeZone;
    private String language;
}
