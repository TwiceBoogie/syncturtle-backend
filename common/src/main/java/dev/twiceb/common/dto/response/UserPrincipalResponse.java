package dev.twiceb.common.dto.response;

import java.util.List;
import java.util.UUID;

import dev.twiceb.common.enums.UserRole;
import dev.twiceb.common.enums.UserStatus;
import lombok.Data;
// import lombok.EqualsAndHashCode;

@Data
public class UserPrincipalResponse {
    private UUID id;
    private String email;
    private boolean verified;
    private UserStatus userStatus;
    private UserRole role;
    private String fullName;
    private List<UserDeviceResponse> userDevices;
}
