package dev.twiceb.passwordservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.core.VaultOperations;

import dev.twiceb.passwordservice.model.support.CryptoPort;
import dev.twiceb.passwordservice.model.support.VaultCryptoAdapter;

@Configuration
public class CryptoConfig {

    @Bean
    CryptoPort cryptoPort(VaultOperations ops, @Value("${app.vault.transit.backend:transit}") String backend) {
        return new VaultCryptoAdapter(ops, backend);
    }
}
