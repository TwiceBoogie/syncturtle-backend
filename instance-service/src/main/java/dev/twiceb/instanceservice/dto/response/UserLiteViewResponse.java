package dev.twiceb.instanceservice.dto.response;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Data
public class UserLiteViewResponse {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String displayName;
    private Instant dateJoined;
}
