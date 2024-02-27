package dev.twiceb.userservice.dto.request;

import lombok.Data;

@Data
public class SettingsRequest {
    private String username;
    private String email;
    private String countryCode;
    private Long phone;
    private String gender;
}
