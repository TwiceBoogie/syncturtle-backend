package dev.twiceb.userservice.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetadataDto {
    private String userAgent;
    private String ipAddress;
    private String referer;
    private String acceptLanguage;
    private String httpMethod;
}
