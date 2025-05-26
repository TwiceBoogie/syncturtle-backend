package dev.twiceb.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChangeEvent {
    private LocalDateTime expirationTime; // The expiration timer of the old password in vault
    private UUID deviceKeyId;
}
