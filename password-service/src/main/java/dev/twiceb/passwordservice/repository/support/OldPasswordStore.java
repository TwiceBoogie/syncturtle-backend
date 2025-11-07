package dev.twiceb.passwordservice.repository.support;

import java.util.Optional;
import java.util.UUID;

import dev.twiceb.passwordservice.dto.request.OldPasswordDTO;
import dev.twiceb.passwordservice.model.Keychain;

public interface OldPasswordStore {
    OldPasswordDTO saveOldPassword(Keychain keychain);

    Optional<String> findLastPassword(UUID userId);
}
