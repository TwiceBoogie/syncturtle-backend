package dev.twiceb.common.dto.context;

import java.util.UUID;

public final class AuthContext {
    private static final ThreadLocal<UUID> CURRENT = new InheritableThreadLocal<>();

    private AuthContext() {}

    public static void set(UUID id) {
        CURRENT.set(id);
    }

    public static UUID get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
