package dev.twiceb.userservice.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class AuthUserResponse {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
}
