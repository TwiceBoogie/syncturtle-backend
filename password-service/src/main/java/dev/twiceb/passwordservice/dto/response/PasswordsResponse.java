package dev.twiceb.passwordservice.dto.response;

import java.util.Date;

import dev.twiceb.passwordservice.enums.DomainStatus;
import lombok.Data;

@Data
public class PasswordsResponse {
    private Long id;
    private String domain;
    private String fakePassword;
    private Date date;
    private DomainStatus status;
}
