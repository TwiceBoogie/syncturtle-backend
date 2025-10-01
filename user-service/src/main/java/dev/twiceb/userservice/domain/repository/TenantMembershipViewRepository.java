package dev.twiceb.userservice.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import dev.twiceb.userservice.domain.model.readmodel.TenantMembershipView;
import dev.twiceb.userservice.domain.model.readmodel.TenantMembershipViewId;

public interface TenantMembershipViewRepository
        extends JpaRepository<TenantMembershipView, TenantMembershipViewId> {

    // redirect hotpath; does user have an active membership in this tenant
    boolean existsById_TenantIdAndId_UserIdAndActiveTrue(UUID tenantId, UUID userId);

    boolean existsByIdUserIdAndActiveTrue(UUID userId);

    // first (oldest) active membership for a user (fallback redirect)
    Optional<TenantMembershipView> findFirstByIdUserIdAndActiveTrueOrderByCreatedAtAsc(UUID userId);

    // all memberships for a user
    Page<TenantMembershipView> findByIdUserId(UUID userId, Pageable pageable);

    // roles a user has accross tenants
    List<TenantMembershipView> findByIdUserIdAndActiveTrue(UUID userId);

    // prefer last tenant if memberhsip exist, else earlist active membership
    @Query("""
            select t.slug
            from TenantMembershipView m
            join TenantView t on t.id = m.id.tenantId
            where m.id.userId = :userId and m.active = true
            order by case
                when (:lastTenantId is not null and m.id.tenantId = :lastTenantId) then 0
                else 1
                end,
                m.createdAt asc
            """)
    List<String> findPrefferedSlug(UUID userId, UUID lastTenantId, Pageable pageable);
}
