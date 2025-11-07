package dev.twiceb.passwordservice.config;

import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.repository.configuration.EnableVaultRepositories;

import dev.twiceb.passwordservice.dto.request.OldPasswordDTO;
import dev.twiceb.passwordservice.model.Keychain;
import dev.twiceb.passwordservice.repository.OldPasswordRepository;
import dev.twiceb.passwordservice.repository.support.OldPasswordStore;

@Configuration
@EnableVaultRepositories(basePackageClasses = OldPasswordRepository.class)
@ConditionalOnProperty(prefix = "spring.cloud.vault", name = "enabled", havingValue = "true")
public class VaultRepositoryConfig {

    @Bean
    OldPasswordStore vaultOldPasswordStore(OldPasswordRepository repo) {
        return new OldPasswordStore() {

            @Override
            public OldPasswordDTO saveOldPassword(Keychain keychain) {
                OldPasswordDTO payload = new OldPasswordDTO();
                payload.setId(String.valueOf(keychain.getId()));
                payload.setPassword(Base64.getEncoder().encodeToString(keychain.getPassword()));
                payload.setDekId(String.valueOf(keychain.getEncryptionKey().getId()));
                payload.setTtl("1h");
                payload.setVector(Base64.getEncoder().encodeToString(keychain.getVector()));
                payload.setTimestamp(Instant.now().toString());

                return repo.save(payload);
            }

            @Override
            public Optional<String> findLastPassword(UUID userId) {
                return repo.findTopByUserIdOrderByCreatedAtDesc(userId).map(OldPasswordDTO::getPassword);
            }

        };
    }
}
