package dev.twiceb.common.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserPrincipleResponse extends BaseMessage {
    private Long id;
    private String email;
    private boolean active;
    private String role;
    private String fullName;

    public UserPrincipleResponse() {
        super(UserPrincipleResponse.class.getSimpleName());
    }
}
