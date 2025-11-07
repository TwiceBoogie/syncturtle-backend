package dev.twiceb.userservice.configurations;

import com.zaxxer.hikari.HikariConfigMXBean;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;
import org.springframework.vault.core.lease.event.SecretLeaseExpiredEvent;

/**
 * This class manages lease rotation of dynamic database credentials obtained
 * from HashiCorpVault
 */
// https://itnext.io/how-to-rotate-expired-spring-cloud-vault-relational-db-credentials-without-restarting-the-app-66976fbb4bbe
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.cloud.vault", name = "enabled", havingValue = "true")
public class VaultLeaseConfig {

    @Value("${spring.cloud.vault.database.role:}")
    private String databaseRole;

    private final ApplicationContext applicationContext;

    @PostConstruct
    private void postConstruct() {
        final String vaultCredsPath = "database/creds/%s".formatted(databaseRole);
        SecretLeaseContainer leaseContainer = applicationContext.getBean(SecretLeaseContainer.class);

        // start in rotate mode
        leaseContainer.addRequestedSecret(RequestedSecret.rotating(vaultCredsPath));

        leaseContainer.addLeaseListener(leaseEvent -> {
            // if paths don't match do nothing;
            if (!vaultCredsPath.equals(leaseEvent.getSource().getPath())) {
                return;
            }

            // check if event published (contains our new creds) and if mode is rotate
            if (leaseEvent instanceof SecretLeaseCreatedEvent secretLeaseCreatedEvent
                    && leaseEvent.getSource().getMode() == RequestedSecret.Mode.ROTATE) {
                String username = (String) secretLeaseCreatedEvent.getSecrets().get("username");
                String password = (String) secretLeaseCreatedEvent.getSecrets().get("password");

                log.info("==> Applying rotated db creds: {}", username);
                // keep in sync so framework can read them at runtime
                System.setProperty("spring.datasource.username", username);
                System.setProperty("spring.datasource.password", password);

                updateDataSource(username, password);
                // if its an expired event, try again
            } else if (leaseEvent instanceof SecretLeaseExpiredEvent) {
                log.warn("==> Lease expired for path: {}. Reregistering rotation", vaultCredsPath);
                try {
                    leaseContainer.requestRotatingSecret(vaultCredsPath);
                } catch (Exception e) {
                    log.error("==> Failed to reregister rotation: {}", e.getMessage(), e);
                }
            }
        });
    }

    /***
     * Updates the HikariDataSource bean with new database credentials. It performs
     * a soft eviction
     * of database connections to force the use of updated creds. The
     * HikariConfigMXBean is then
     * used to update the username and password in the HikariDataSource.
     * 
     * @param username username extracted from the lease event's secrets
     * @param password password extracted from the lease event's secrets
     */
    private void updateDataSource(String username, String password) {
        HikariDataSource dataSource = (HikariDataSource) applicationContext.getBean("dataSource");

        HikariPoolMXBean pool = dataSource.getHikariPoolMXBean();
        if (pool != null) {
            log.info("==> Soft evict database connections");
            pool.softEvictConnections();
        }

        log.info("==> Update database credentials");
        HikariConfigMXBean config = dataSource.getHikariConfigMXBean();
        config.setUsername(username);
        config.setPassword(password);
    }
}
