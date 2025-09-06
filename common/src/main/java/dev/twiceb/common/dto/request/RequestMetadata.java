package dev.twiceb.common.dto.request;

import java.util.LinkedHashMap;
import java.util.Map;
import dev.twiceb.common.dto.request.util.MetadataHeaders;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestMetadata {
    private String requestId;
    private String correlationId;
    private String ipAddress;
    private String domain;
    private String userAgent;
    private String referer;
    private String acceptLanguage;
    private String httpMethod;
    private String deviceId;

    public Map<String, String> toHeaders() {
        Map<String, String> h = new LinkedHashMap<>();
        if (requestId != null) {
            h.put(MetadataHeaders.REQUEST_ID, requestId);
        }
        if (correlationId != null) {
            h.put(MetadataHeaders.CORRELATION_ID, correlationId);
        }
        if (ipAddress != null) {
            h.put(MetadataHeaders.FORWARDED_FOR, ipAddress);
        }
        if (domain != null) {
            h.put(MetadataHeaders.FORWARDED_HOST, domain);
        }
        if (userAgent != null) {
            h.put(MetadataHeaders.USER_AGENT, userAgent);
        }
        if (referer != null) {
            h.put(MetadataHeaders.REFERER, referer);
        }
        if (acceptLanguage != null) {
            h.put(MetadataHeaders.ACCEPT_LANGUAGE, acceptLanguage);
        }

        return h;
    }
}
