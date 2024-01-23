package dev.twiceb.passwordservice.repository;

import dev.twiceb.passwordservice.model.PasswordExpiryPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PasswordExpiryPolicyRepository extends JpaRepository<PasswordExpiryPolicy, Long> {

    @Query("SELECT pep FROM PasswordExpiryPolicy pep WHERE pep.policyName = :policyName")
    PasswordExpiryPolicy findByPolicyName(@Param("policyName") String policyName);

    @Query("SELECT CASE WHEN COUNT(pep) > 0 THEN true ELSE false END FROM PasswordExpiryPolicy pep WHERE pep.policyName = :policyName")
    boolean existsByPolicyName(@Param("policyName") String policyName);

    @Query("SELECT CASE WHEN COUNT(pep) > 0 THEN true ELSE false END FROM PasswordExpiryPolicy pep WHERE pep.maxExpiryDays = :maxExpiryDays AND pep.notificationDays = :notificationDays")
    boolean existsByExpiryAndNotificationDays(@Param("maxExpiryDays") int maxExpiryDays, @Param("notificationDays") int notificationDays);
}
