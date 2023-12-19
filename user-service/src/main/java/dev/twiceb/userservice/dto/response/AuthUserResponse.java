package dev.twiceb.userservice.dto.response;

import lombok.Data;

@Data
public class AuthUserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
}
