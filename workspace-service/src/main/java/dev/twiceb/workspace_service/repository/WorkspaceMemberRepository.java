package dev.twiceb.workspace_service.repository;

import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.twiceb.workspace_service.enums.WorkspaceRole;
import dev.twiceb.workspace_service.model.WorkspaceMember;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, UUID> {

    boolean existsByWorkspace_SlugIgnoreCaseAndMemberIdAndActiveTrue(String slug, UUID memberId);

    boolean existsByWorkspace_SlugIgnoreCaseAndMemberIdAndRoleInAndActiveTrue(String slug,
            UUID memberId, Set<WorkspaceRole> roles);

    long countByWorkspace_IdAndActiveTrue(UUID workspaceId);
}
