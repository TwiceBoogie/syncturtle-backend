package dev.twiceb.passwordservice.config;

import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.twiceb.passwordservice.dto.request.OldPasswordDTO;
import dev.twiceb.passwordservice.model.Keychain;
import dev.twiceb.passwordservice.repository.support.OldPasswordStore;

@Configuration
@ConditionalOnMissingBean(OldPasswordStore.class)
public class NoVaultOldPasswordConfig {

    @Bean
    OldPasswordStore noopOldPasswordStore() {
        return new OldPasswordStore() {

            @Override
            public OldPasswordDTO saveOldPassword(Keychain keychain) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'saveOldPassword'");
            }

            @Override
            public Optional<String> findLastPassword(UUID userId) {
                return Optional.empty();
            }

        };
    }
}
