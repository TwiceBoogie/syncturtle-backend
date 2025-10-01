package dev.twiceb.workspace_service.service;

import java.util.UUID;

public interface PermissionService {
    boolean isActiveMember(UUID userId, String slug);

    boolean isAdmin(UUID userId, String slug);

    boolean isAdminOrMember(UUID userId, String slug);
}
