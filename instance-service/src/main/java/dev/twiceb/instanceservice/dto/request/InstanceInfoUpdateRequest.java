package dev.twiceb.instanceservice.dto.request;

import lombok.Data;

@Data
public class InstanceInfoUpdateRequest {
    private String instanceName;
    private String domain;
    private String namespace;
}
