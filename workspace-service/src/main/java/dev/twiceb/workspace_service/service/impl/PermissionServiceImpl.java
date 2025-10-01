package dev.twiceb.workspace_service.service.impl;

import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Component;
import dev.twiceb.workspace_service.enums.WorkspaceRole;
import dev.twiceb.workspace_service.repository.WorkspaceMemberRepository;
import dev.twiceb.workspace_service.service.PermissionService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    /// maps WorkspaceBasePermission, WorkspaceAdminPermission
    private final WorkspaceMemberRepository wsMemberRepository;

    @Override
    public boolean isActiveMember(UUID userId, String slug) {
        return wsMemberRepository.existsByWorkspace_SlugIgnoreCaseAndMemberIdAndActiveTrue(slug,
                userId);
    }

    @Override
    public boolean isAdmin(UUID userId, String slug) {
        return wsMemberRepository.existsByWorkspace_SlugIgnoreCaseAndMemberIdAndRoleInAndActiveTrue(
                slug, userId, Set.of(WorkspaceRole.ADMIN));
    }

    @Override
    public boolean isAdminOrMember(UUID userId, String slug) {
        return wsMemberRepository.existsByWorkspace_SlugIgnoreCaseAndMemberIdAndRoleInAndActiveTrue(
                slug, userId, Set.of(WorkspaceRole.MEMBER));
    }

}
