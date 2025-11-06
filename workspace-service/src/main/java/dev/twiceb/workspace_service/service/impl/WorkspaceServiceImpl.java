package dev.twiceb.workspace_service.service.impl;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.twiceb.common.dto.context.AuthContext;
import dev.twiceb.workspace_service.dto.request.CreateWorkspaceRequest;
import dev.twiceb.workspace_service.enums.WorkspaceRole;
import dev.twiceb.workspace_service.model.Workspace;
import dev.twiceb.workspace_service.model.WorkspaceMember;
import dev.twiceb.workspace_service.repository.WorkspaceMemberRepository;
import dev.twiceb.workspace_service.repository.WorkspaceRepository;
import dev.twiceb.workspace_service.repository.projections.WorkspaceMeProjection;
import dev.twiceb.workspace_service.repository.projections.WorkspaceProjection;
import dev.twiceb.workspace_service.service.WorkspaceService;
import dev.twiceb.workspace_service.utils.ValidationUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository wMemberRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public List<WorkspaceMeProjection> createWorkspaceWithOwner(CreateWorkspaceRequest request) {
        ValidationUtils.validateName(request.getName());
        ValidationUtils.validateSlug(request.getSlug());

        // 1; create workspace;
        Workspace ws = new Workspace();
        ws.setName(request.getName());
        ws.setSlug(request.getSlug());
        ws.setOrganizationSize(request.getOrganizationSize());
        ws.setOwnerId(AuthContext.get());

        ws = workspaceRepository.save(ws);
        // 2; create workspace member
        WorkspaceMember wsm = new WorkspaceMember();
        wsm.setWorkspace(ws);
        wsm.setMemberId(AuthContext.get());
        wsm.setRole(WorkspaceRole.ADMIN);
        wsm.setCompanyRole(request.getCompanyRole());
        wsm.setActive(true);

        wMemberRepository.save(wsm);

        return workspaceRepository.findMyWorkspaces(AuthContext.get(), ws.getName());
    }

    @Override
    @Transactional
    public void softDeleteWorkspace(UUID id) {
        // 1; load the entity (still visible due to @Where; use a custom finder that bypasses @Where
        // if needed)
        Workspace ws = workspaceRepository.findByIdIncludingDeleted(id).orElseThrow();
        if (ws.getDeletedAt() != null)
            return; // already deleted

        // 2; rename slug first to free it
        String original = ws.getSlug();
        String newSlug = original + "__" + Instant.now().getEpochSecond();
        ws.setSlug(newSlug);
        workspaceRepository.saveAndFlush(ws);

        // 3; perform soft delete (sets deleted_at via @SQLDelete)
        workspaceRepository.deleteById(ws.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkspaceProjection> getAllWorkspaces(String cursor, int perPage, String search) {
        // 1: first we decode cursor which contain (createdAt, Id)
        CursorPayload payload = decode(cursor);
        // perPage + 1 to look ahead
        Pageable pageable = PageRequest.of(0, perPage + 1);

        // build pattern to avoid db side concatenation
        String pattern = (search == null || search.isBlank()) ? null
                : "%" + search.trim().toLowerCase() + "%";

        if (payload == null) {
            return workspaceRepository.findWorkspacesFirstPage(pattern);
        } else {
            return workspaceRepository.findWorkspacesAfter(payload.getId(), payload.getCreatedAt(),
                    pattern, pageable);
        }
    }

    private CursorPayload decode(String s) {
        if (s == null || s.isBlank()) {
            log.debug("[cursor] empty -> null (first page)");
            return null;
        }

        try {
            // check to see if its a certain length
            String padded = switch (s.length()) {
                case 2 -> s + "==";
                case 3 -> s + "=";
                default -> s;
            };

            String text = new String(Base64.getUrlDecoder().decode(padded), StandardCharsets.UTF_8);

            JsonNode node = objectMapper.readTree(text);

            if (!node.hasNonNull("createdAt") || !node.hasNonNull("id")) {
                log.warn("[cursor] missing fields -> {}", text);
                return null;
            }

            Instant createdAt = Instant.parse(node.get("createdAt").asText());
            UUID id = UUID.fromString(node.get("id").asText());

            CursorPayload payload = new CursorPayload();
            payload.setCreatedAt(createdAt);
            payload.setId(id);
            return payload;
        } catch (Exception e) {
            log.warn("[cursor] failed to decode; treating as first page. s='{}'", s, e);
            return null;
        }
    }

    @Data
    public static class CursorPayload {
        private UUID id; // last id
        private Instant createdAt; // last createdAt
    }
}
