package dev.twiceb.userservice.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.twiceb.userservice.domain.model.LoginPolicy;

public interface LoginPolicyRepository extends JpaRepository<LoginPolicy, Long> {
    Optional<LoginPolicy> findByIsDefaultTrue();

    Optional<LoginPolicy> findByPolicyName(String policyName);
}
