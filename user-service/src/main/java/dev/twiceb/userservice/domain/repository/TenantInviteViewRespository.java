package dev.twiceb.userservice.domain.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.twiceb.userservice.domain.model.readmodel.TenantInviteView;
import dev.twiceb.userservice.domain.model.readmodel.TenantInviteViewId;

public interface TenantInviteViewRespository
        extends JpaRepository<TenantInviteView, TenantInviteViewId> {

    // count pending invite for an email
    long countByIdEmailAndAcceptedFalse(String email);

    long countByIdEmailIgnoreCaseAndAcceptedFalse(String email);

    // pending invites for specific tenant/email
    boolean existsByIdTenantIdAndIdEmailAndAcceptedFalse(UUID tenantId, String email);

    List<TenantInviteView> findAllByIdEmailAndAcceptedFalse(String email);
}
