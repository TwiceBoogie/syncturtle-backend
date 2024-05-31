package dev.twiceb.common.dto.response;

import java.util.List;

import dev.twiceb.common.enums.UserRole;
import dev.twiceb.common.enums.UserStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class UserPrincipleResponse {
    private Long id;
    private String username;
    private boolean verified;
    private UserStatus userStatus;
    private UserRole role;
    private String fullName;
    private List<UserDeviceResponse> userDevices;
}
