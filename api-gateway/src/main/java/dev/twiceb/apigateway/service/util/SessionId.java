package dev.twiceb.apigateway.service.util;

import java.util.UUID;

public final class SessionId {
    public static String random() {
        return UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().replace("-", "");
    }
}
