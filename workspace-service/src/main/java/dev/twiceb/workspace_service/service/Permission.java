package dev.twiceb.workspace_service.service;

import java.util.Map;
import java.util.UUID;
import jakarta.servlet.http.HttpServletRequest;

public interface Permission {
    boolean hasPermission(HttpServletRequest request, UUID userId, Map<String, String> pathVars);
}
