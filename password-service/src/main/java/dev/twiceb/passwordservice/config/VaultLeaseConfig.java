package dev.twiceb.passwordservice.config;

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

    @Value("${spring.cloud.vault.database.role}")
    private String databaseRole;

    private final ApplicationContext applicationContext;

    @PostConstruct
    SecretLeaseContainer secretLeaseContainer() {
        final String vaultCredsPath = String.format("database/creds/%s", databaseRole);
        SecretLeaseContainer leaseContainer = applicationContext.getBean(SecretLeaseContainer.class);

        leaseContainer.addLeaseListener(leaseEvent -> {
            if (vaultCredsPath.equals(leaseEvent.getSource().getPath())) {
                log.info("==> Received event: {}", leaseEvent);

                if (leaseEvent.getSource().getMode() == RequestedSecret.Mode.RENEW) {
                    log.info("==> Replace RENEW lease by a rotate one.");
                    leaseContainer.requestRotatingSecret(vaultCredsPath);
                } else if (leaseEvent instanceof SecretLeaseCreatedEvent secretLeaseCreatedEvent &&
                        leaseEvent.getSource().getMode() == RequestedSecret.Mode.ROTATE) {
                    String username = (String) secretLeaseCreatedEvent.getSecrets().get("username");
                    String password = (String) secretLeaseCreatedEvent.getSecrets().get("password");

                    log.info("==> Update System properties username & password");
                    System.setProperty("spring.datasource.username", username);
                    System.setProperty("spring.datasource.password", password);

                    log.info("==> spring.datasource.username: {}", username);

                    updateDataSource(username, password);
                } else if (leaseEvent instanceof SecretLeaseExpiredEvent) {
                    log.warn("==> Lease expired for path: {}", leaseEvent.getSource().getPath());

                    try {
                        leaseContainer.requestRotatingSecret(vaultCredsPath);
                    } catch (Exception e) {
                        log.error("==> Failed to request new Vault credentials after lease expiry: {}", e.getMessage());
                    }
                }
            }
        });
        return leaseContainer;
    }

    /***
     * Updates the HikariDataSource bean with new database credentials.
     * It performs a soft eviction of database connections to force the use
     * of updated creds. The HikariConfigMXBean is then used to update
     * the username and password in the HikariDataSource.
     * 
     * @param username username extracted from the lease event's secrets
     * @param password password extracted from the lease event's secrets
     */
    private void updateDataSource(String username, String password) {
        HikariDataSource hikariDataSource = (HikariDataSource) applicationContext.getBean("dataSource");

        log.info("==> Soft evict database connections");
        HikariPoolMXBean hikariPoolMXBean = hikariDataSource.getHikariPoolMXBean();
        if (hikariPoolMXBean != null) {
            hikariPoolMXBean.softEvictConnections();
        }

        log.info("==> Update database credentials");
        HikariConfigMXBean hikariConfigMXBean = hikariDataSource.getHikariConfigMXBean();
        hikariConfigMXBean.setUsername(username);
        hikariConfigMXBean.setPassword(password);
    }
}
