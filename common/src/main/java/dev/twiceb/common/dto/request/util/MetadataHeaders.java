package dev.twiceb.common.dto.request.util;

// canonical header contract
public final class MetadataHeaders {
    public static final String REQUEST_ID = "X-Request-Id";
    public static final String CORRELATION_ID = "X-Correlation-Id";
    public static final String FORWARDED_FOR = "X-Forwarded-For";
    public static final String FORWARDED_HOST = "X-Forwarded-Host";
    public static final String REFERER = "Referer";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String METHOD = "X-Http-Method";
    public static final String HOST = "Host";
    public static final String USER_AGENT = "User-Agent";

    private MetadataHeaders() {}
}
