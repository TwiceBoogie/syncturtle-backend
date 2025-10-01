package dev.twiceb.workspace_service.configuration;

import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import dev.twiceb.common.dto.context.AuthContext;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class AuditConfig {

    @Bean
    AuditorAware<UUID> auditorAware() {
        return () -> Optional.ofNullable(AuthContext.get());
    }
}
