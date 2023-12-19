package dev.twiceb.passwordsservice.dto.response;

import java.util.Date;

import dev.twiceb.passwordsservice.enums.DomainStatus;
import lombok.Data;

@Data
public class AllPasswordsResponse {
    private Long id;
    private String domain;
    private String fakePassword;
    private Date date;
    private DomainStatus status;
}
