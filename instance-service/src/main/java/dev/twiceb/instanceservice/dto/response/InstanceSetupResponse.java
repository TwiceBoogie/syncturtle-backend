package dev.twiceb.instanceservice.dto.response;

import lombok.Data;

@Data
public class InstanceSetupResponse {
    private ConfigDataResponse config;
    private InstanceInfoResponse instance;
}
