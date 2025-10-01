package dev.twiceb.userservice.dto.response;

import java.util.UUID;
import lombok.Data;

@Data
public class UserMeResponse {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String mobilePhone;
    private String displayName;
    private String lastLoginMedium;
    private boolean isPasswordAutoSet;
    private boolean isEmailVerified;
    private boolean isActive;
}
