package dev.twiceb.common.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateEvent {
    private String requestId;
    private String email;
    private String firstName;
    private String lastName;
    private String passwordCipherText;
    private String companyName;
}


// {
// "requestId": "db2fe39b-7b7a-4f13-8d3a-9306b7b4e0f8",
// "email": "admin@example.com",
// "firstName": "Ada",
// "lastName": "Lovelace",
// "password": "StrongPassword!23",
// "companyName": "SyncTurtle Inc",
// "metadata": {
// "requestedBy": "system",
// "ip": "203.0.113.7",
// "userAgent": "setup-wizard/1.0"
// }
// }
