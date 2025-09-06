package dev.twiceb.common.dto.context;

import dev.twiceb.common.dto.request.RequestMetadata;

public final class RequestMetadataContext {
    private static final ThreadLocal<RequestMetadata> CTX = new InheritableThreadLocal<>();

    private RequestMetadataContext() {}

    public static void set(RequestMetadata md) {
        CTX.set(md);
    }

    public static RequestMetadata get() {
        return CTX.get();
    }

    public static void clear() {
        CTX.remove();
    }
}
