package dev.twiceb.userservice.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetadataDto {
    private String userAgent;
    private String ipAddress;
    private String domain;
    private String requestId;
    private String correlationId;
    private String referer;
    private String acceptLanguage;
    private String httpMethod;
    private String deviceKey;
}
