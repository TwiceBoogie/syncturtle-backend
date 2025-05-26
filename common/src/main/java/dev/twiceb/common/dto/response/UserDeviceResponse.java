package dev.twiceb.common.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class UserDeviceResponse {
    private UUID id;
    private String deviceKey;
}
