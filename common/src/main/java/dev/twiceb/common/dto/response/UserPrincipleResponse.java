package dev.twiceb.common.dto.response;

import dev.twiceb.common.enums.UserRole;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserPrincipleResponse extends BaseMessage {
    private Long id;
    private String email;
    private boolean verified;
    private String userStatus;
    private UserRole role;
    private String fullName;

    public UserPrincipleResponse() {
        super(UserPrincipleResponse.class.getSimpleName());
    }
}
