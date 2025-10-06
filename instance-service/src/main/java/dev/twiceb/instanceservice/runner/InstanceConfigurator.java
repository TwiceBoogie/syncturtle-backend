package dev.twiceb.instanceservice.runner;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import dev.twiceb.instanceservice.domain.model.InstanceConfiguration;
import dev.twiceb.instanceservice.domain.repository.InstanceConfigurationRepository;
import dev.twiceb.instanceservice.service.util.ConfigurationHelper;
import dev.twiceb.instanceservice.service.util.ConfigurationHelper.InsertPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstanceConfigurator {

    @Qualifier("encryptorBean")
    private final StringEncryptor encryptor;
    private final InstanceConfigurationRepository iConfigurationRepository;
    private final ConfigurationHelper cHelper;

    private static final Set<InstanceConfigurationKey> MANAGED_KEYS = EnumSet.of(
            // auth / workspace
            InstanceConfigurationKey.ENABLE_SIGNUP, InstanceConfigurationKey.ENABLE_EMAIL_PASSWORD,
            InstanceConfigurationKey.ENABLE_MAGIC_LINK_LOGIN,

            // smtp
            InstanceConfigurationKey.ENABLE_SMTP, InstanceConfigurationKey.EMAIL_HOST,
            InstanceConfigurationKey.EMAIL_HOST_USER, InstanceConfigurationKey.EMAIL_HOST_PASSWORD,
            InstanceConfigurationKey.EMAIL_PORT, InstanceConfigurationKey.EMAIL_FROM,
            InstanceConfigurationKey.EMAIL_USE_TLS, InstanceConfigurationKey.EMAIL_USE_SSL,

            // google / github / gitlab
            InstanceConfigurationKey.GOOGLE_CLIENT_ID,
            InstanceConfigurationKey.GOOGLE_CLIENT_SECRET,
            InstanceConfigurationKey.GITHUB_CLIENT_ID,
            InstanceConfigurationKey.GITHUB_CLIENT_SECRET, InstanceConfigurationKey.GITLAB_HOST,
            InstanceConfigurationKey.GITLAB_CLIENT_ID,
            InstanceConfigurationKey.GITLAB_CLIENT_SECRET);

    // private static final List<InstanceConfigurationKey> INTEGRATION_FLAGS = List.of(
    // InstanceConfigurationKey.IS_GOOGLE_ENABLED, InstanceConfigurationKey.IS_GITHUB_ENABLED,
    // InstanceConfigurationKey.IS_GITLAB_ENABLED);

    @Transactional
    public void run() {
        // 1; mandatory checks
        cHelper.ensureMandatorySecretsPresentOrThrow();

        // 2; seed managed keys using appProperties (no db read; idempotent upsert)
        seedManagedKeys();

        // 3; derived flags
        ensureFlag(InstanceConfigurationKey.IS_GOOGLE_ENABLED, () -> cHelper
                .nonEmpty(cHelper.resolveValue(InstanceConfigurationKey.GOOGLE_CLIENT_ID))
                && cHelper.nonEmpty(
                        cHelper.resolveValue(InstanceConfigurationKey.GOOGLE_CLIENT_SECRET)));

        ensureFlag(InstanceConfigurationKey.IS_GITHUB_ENABLED, () -> cHelper
                .nonEmpty(cHelper.resolveValue(InstanceConfigurationKey.GITHUB_CLIENT_ID))
                && cHelper.nonEmpty(
                        cHelper.resolveValue(InstanceConfigurationKey.GITHUB_CLIENT_SECRET)));

        ensureFlag(InstanceConfigurationKey.IS_GITLAB_ENABLED, () -> cHelper
                .nonEmpty(cHelper.resolveValue(InstanceConfigurationKey.GITLAB_HOST))
                && cHelper.nonEmpty(cHelper.resolveValue(InstanceConfigurationKey.GITLAB_CLIENT_ID))
                && cHelper.nonEmpty(
                        cHelper.resolveValue(InstanceConfigurationKey.GITLAB_CLIENT_SECRET)));
        log.info("Instance configuration bootstrap complete.");

    }

    private void seedManagedKeys() {
        List<InstanceConfiguration> existing = iConfigurationRepository.findByKeyIn(MANAGED_KEYS);
        Set<InstanceConfigurationKey> existingKeys =
                existing.stream().map(InstanceConfiguration::getKey).collect(Collectors.toSet());

        // build missing keys
        List<InstanceConfiguration> missing =
                MANAGED_KEYS.stream().filter(k -> !existingKeys.contains(k)).map(k -> {
                    InsertPayload payload = cHelper.buildInsertPayload(k);
                    InstanceConfiguration ic = new InstanceConfiguration();
                    ic.setKey(k);
                    ic.setCategory(payload.getCategory());
                    ic.setEncrypted(payload.isEncrypted());
                    ic.setValue(payload.getValue());
                    return ic;
                }).toList();

        if (!missing.isEmpty()) {
            for (InstanceConfiguration ic : missing) {
                try {
                    iConfigurationRepository.save(ic);
                } catch (DataIntegrityViolationException e) {
                    // ignore
                }
            }
            log.info("Seeded {} managed config keys (existing rows untouched).", missing.size());
        } else {
            log.info("All managed config keys already present; nothing to seed");
        }
    }

    private void ensureFlag(InstanceConfigurationKey flagKey, BooleanSupplier enableSupplier) {
        if (iConfigurationRepository.existsByKey(flagKey)) {
            log.debug("{} already present; not modifying.", flagKey);
            return;
        }
        boolean enabled = enableSupplier.getAsBoolean();
        cHelper.insertPlainFlag(flagKey, enabled ? "1" : "0", "AUTHENTICATION");
        log.info("Computed {} = {}", flagKey, enabled ? "1" : "0");
    }

}
