package dev.twiceb.userservice.dto.response;

import lombok.Data;

@Data
public class MagicCodeResponse {

    private boolean existing;
    private String status;
    private boolean passwordAutoSet;
}
