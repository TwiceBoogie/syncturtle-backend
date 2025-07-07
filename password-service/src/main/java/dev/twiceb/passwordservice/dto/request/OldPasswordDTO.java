package dev.twiceb.passwordservice.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.vault.repository.mapping.Secret;

import java.time.LocalDateTime;

@Secret
@Getter
@Setter
public class OldPasswordDTO {
    @Id
    private String id;
    private String dekId;
    private String password;
    private String vector;
    private String ttl;
    private String timestamp = LocalDateTime.now().toString();

}
