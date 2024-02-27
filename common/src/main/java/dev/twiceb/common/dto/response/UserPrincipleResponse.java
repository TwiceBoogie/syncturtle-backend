package dev.twiceb.common.dto.response;

import dev.twiceb.common.enums.UserRole;
import dev.twiceb.common.enums.UserStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserPrincipleResponse extends BaseMessage {
    private Long id;
    private String username;
    private boolean verified;
    private UserStatus userStatus;
    private UserRole role;
    private String fullName;

    public UserPrincipleResponse() {
        super(UserPrincipleResponse.class.getSimpleName());
    }
}
