package dev.twiceb.passwordservice.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EncryptionKeysResponse {
    private Long id;
    private String name;
    private String description;
    private String algorithm;
    private LocalDateTime expirationDate;
    private boolean isEnabled;
}
