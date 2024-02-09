package dev.twiceb.passwordservice.repository;

import dev.twiceb.passwordservice.model.PasswordExpiryConfig;
import dev.twiceb.passwordservice.repository.projection.KeychainNotificationProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PasswordExpiryConfigRepository extends JpaRepository<PasswordExpiryConfig, Long> {

//    @Query("SELECT pec FROM PasswordExpiryConfig pec WHERE pec.notificationSent = false AND " +
//            "(pec.expiryDate - pec.passwordExpiryPolicy.notificationDays) >= CURRENT_DATE")
//    List<KeychainNotificationProjection> getPasswordExpiryConfig();
}
