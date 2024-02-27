package dev.twiceb.passwordservice.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OldPasswordDTO {
    private Long id;
    private byte[] password;
    private String dek;
    private String ttl;
    private LocalDateTime timestamp;
}
