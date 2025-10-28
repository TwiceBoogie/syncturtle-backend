package dev.twiceb.common.util;

import java.util.List;
import dev.twiceb.common.enums.UserRole;

public final class ExpandRoles {
    public static List<String> expand(UserRole role) {
        return switch (role) {
            case SUPER_USER -> List.of("SUPER_USER", "ADMIN", "USER");
            case ADMIN -> List.of("ADMIN", "USER");
            case USER -> List.of("USER");
        };
    }
}
