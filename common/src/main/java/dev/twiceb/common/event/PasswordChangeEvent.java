package dev.twiceb.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChangeEvent {
    private Instant expirationTime; // The expiration timer of the old password in vault
    private UUID deviceKeyId;
}
