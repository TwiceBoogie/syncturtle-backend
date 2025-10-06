package dev.twiceb.instanceservice.service.impl;

import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;
import dev.twiceb.common.repository.projections.OnlyId;
import dev.twiceb.instanceservice.domain.repository.InstanceAdminRepository;
import dev.twiceb.instanceservice.domain.repository.InstanceRepository;
import dev.twiceb.instanceservice.service.Permission;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InstanceAdminPermissionImpl implements Permission {

    private final InstanceRepository instanceRepository;
    private final InstanceAdminRepository iAdminRepository;

    @Override
    public boolean hasPermission(HttpServletRequest request, UUID userId,
            Map<String, String> pathVars) {
        String method = request.getMethod();

        if ("GET".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method)) {
            return true;
        }

        if (userId == null) {
            return false;
        }

        UUID instance = instanceRepository.findFirstByOrderByCreatedAtAsc(OnlyId.class)
                .map(OnlyId::getId).orElseThrow();

        return iAdminRepository.existsByInstanceIdAndUserIdAndRoleGreaterThanEqual(instance, userId,
                15);
    }

}
