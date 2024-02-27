package dev.twiceb.passwordservice.repository;

import dev.twiceb.passwordservice.model.RotationPolicy;
import dev.twiceb.passwordservice.repository.projection.ExpiryPolicyProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RotationPolicyRepository extends JpaRepository<RotationPolicy, Long> {

    @Query("SELECT pep FROM RotationPolicy pep WHERE pep.policyName = :policyName")
    RotationPolicy findByPolicyName(@Param("policyName") String policyName);

    @Query("SELECT pep FROM RotationPolicy pep")
    List<ExpiryPolicyProjection> findAllPolicies();

    @Query("SELECT CASE WHEN COUNT(pep) > 0 THEN true ELSE false END FROM RotationPolicy pep WHERE pep.policyName = :policyName")
    boolean existsByPolicyName(@Param("policyName") String policyName);

    @Query("SELECT CASE WHEN COUNT(pep) > 0 THEN true ELSE false END FROM RotationPolicy pep WHERE pep.maxExpiryDays = :maxExpiryDays AND pep.notificationDays = :notificationDays")
    boolean existsByExpiryAndNotificationDays(@Param("maxExpiryDays") int maxExpiryDays, @Param("notificationDays") int notificationDays);
}
