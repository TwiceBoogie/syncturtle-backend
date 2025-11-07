package dev.twiceb.passwordservice.model.support;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CryptoInitializer {
    private final CryptoPort crypto;

    @PostConstruct
    void init() {
        CryptoHolder.set(crypto);
    }
}
