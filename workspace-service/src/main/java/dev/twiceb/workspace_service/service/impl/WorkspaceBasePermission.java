package dev.twiceb.workspace_service.service.impl;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Component;
import dev.twiceb.workspace_service.enums.WorkspaceRole;
import dev.twiceb.workspace_service.repository.WorkspaceMemberRepository;
import dev.twiceb.workspace_service.service.Permission;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WorkspaceBasePermission implements Permission {

    private final WorkspaceMemberRepository wsMemberRepository;

    @Override
    public boolean hasPermission(HttpServletRequest request, UUID userId,
            Map<String, String> pathVars) {
        if (userId == null) {
            return false;
        }
        String slug = pathVars.get("slug");
        if (slug == null)
            return false;

        String method = request.getMethod();

        // safe methods; any active member
        if ("GET".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method)) {
            return wsMemberRepository.existsByWorkspace_SlugIgnoreCaseAndMemberIdAndActiveTrue(slug,
                    userId);
        }

        // admins + members
        if ("PATCH".equals(method) || "PUT".equals(method)) {
            return wsMemberRepository
                    .existsByWorkspace_SlugIgnoreCaseAndMemberIdAndRoleInAndActiveTrue(slug, userId,
                            Set.of(WorkspaceRole.ADMIN, WorkspaceRole.MEMBER));
        }

        if ("DELETE".equals(method)) {
            return wsMemberRepository
                    .existsByWorkspace_SlugIgnoreCaseAndMemberIdAndRoleInAndActiveTrue(slug, userId,
                            Set.of(WorkspaceRole.ADMIN));
        }

        return true;
    }

}
