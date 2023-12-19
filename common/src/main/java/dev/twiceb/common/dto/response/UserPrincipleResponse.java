package dev.twiceb.common.dto.response;

import lombok.Data;

@Data
public class UserPrincipleResponse {
    private Long id;
    private String email;
    private boolean active;
    private String role;
}
