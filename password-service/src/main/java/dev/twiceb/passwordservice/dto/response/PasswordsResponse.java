package dev.twiceb.passwordservice.dto.response;

import java.time.LocalDate;

import dev.twiceb.passwordservice.enums.DomainStatus;
import lombok.Data;

@Data
public class PasswordsResponse {
    private Long id;
    private String username;
    private String domain;
    private String fakePassword;
    private LocalDate expiryDate;
    private DomainStatus status;
}
