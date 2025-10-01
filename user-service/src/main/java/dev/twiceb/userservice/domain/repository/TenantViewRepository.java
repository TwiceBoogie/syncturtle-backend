package dev.twiceb.userservice.domain.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import dev.twiceb.userservice.domain.model.readmodel.TenantView;

public interface TenantViewRepository extends JpaRepository<TenantView, UUID> {

    Optional<TenantView> findBySlug(String slug);

    @SuppressWarnings("null")
    boolean existsById(UUID tenantId);

    List<TenantView> findAllByIdIn(Collection<UUID> ids);

    Page<TenantView> findAllByActiveTrue(Pageable pageable);

    @Query("select t.slug from TenantView t where t.id = :tenantId")
    Optional<String> findSlugById(UUID tenantId);

}
