package dev.twiceb.passwordservice.dto.request;

import lombok.Data;

@Data
public class GenerateRandomPasswordRequest {
    private int randomPasswordLength;
}
