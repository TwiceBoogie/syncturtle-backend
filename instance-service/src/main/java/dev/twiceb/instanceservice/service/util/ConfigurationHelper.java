package dev.twiceb.instanceservice.service.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import dev.twiceb.instanceservice.domain.model.InstanceConfiguration;
import dev.twiceb.instanceservice.domain.repository.InstanceConfigurationRepository;
import dev.twiceb.instanceservice.shared.ConfigKeyLookupRecord;
import dev.twiceb.instanceservice.shared.ConfigKeyRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfigurationHelper {

    public static final List<InstanceConfigurationKey> INTEGRATION_FLAGS = List.of(
            InstanceConfigurationKey.IS_GOOGLE_ENABLED, InstanceConfigurationKey.IS_GITHUB_ENABLED,
            InstanceConfigurationKey.IS_GITLAB_ENABLED);

    @Qualifier("encryptorBean")
    private final StringEncryptor encryptor;
    private final InstanceConfigurationRepository iConfigurationRepository;
    private final AppProperties appProperties;

    public List<ConfigKeyRecord> loadEnvConfigKeys() {
        // List<ConfigKeyRecord> mandatoryConfig = List.of(
        // new ConfigKeyRecord("SECRET_KEY", appProperties.getSecretKey(), "SECURITY", true));

        // for (ConfigKeyRecord config : mandatoryConfig) {
        // if (!isNonEmpty(config.value())) {
        // throw new IllegalStateException(
        // config.key() + " config value is required but not set.");
        // }
        // }

        return List.of(
                new ConfigKeyRecord(InstanceConfigurationKey.ENABLE_SIGNUP,
                        appProperties.getConfigKeys().getEnableSignup(), "AUTHENTICATION", false),
                new ConfigKeyRecord(InstanceConfigurationKey.ENABLE_EMAIL_PASSWORD,
                        appProperties.getConfigKeys().getEnableEmailPassword(), "AUTHENTICATION",
                        false),
                new ConfigKeyRecord(InstanceConfigurationKey.ENABLE_SMTP,
                        appProperties.getConfigKeys().getEnableSmtp(), "SMTP", false),
                new ConfigKeyRecord(InstanceConfigurationKey.EMAIL_HOST,
                        appProperties.getConfigKeys().getEmailHost(), "SMTP", false),
                new ConfigKeyRecord(InstanceConfigurationKey.EMAIL_HOST_USER,
                        appProperties.getConfigKeys().getEmailHostUser(), "SMTP", false),
                new ConfigKeyRecord(InstanceConfigurationKey.EMAIL_HOST_PASSWORD,
                        appProperties.getConfigKeys().getEmailHostPassword(), "SMTP", true),
                new ConfigKeyRecord(InstanceConfigurationKey.EMAIL_PORT,
                        appProperties.getConfigKeys().getEmailPort(), "SMTP", false),
                new ConfigKeyRecord(InstanceConfigurationKey.EMAIL_FROM,
                        appProperties.getConfigKeys().getEmailFrom(), "SMTP", false),
                new ConfigKeyRecord(InstanceConfigurationKey.EMAIL_USE_TLS,
                        appProperties.getConfigKeys().getEmailUseTls(), "SMTP", false),
                new ConfigKeyRecord(InstanceConfigurationKey.EMAIL_USE_SSL,
                        appProperties.getConfigKeys().getEmailUseSsl(), "SMTP", false),
                new ConfigKeyRecord(InstanceConfigurationKey.ENABLE_MAGIC_LINK_LOGIN,
                        appProperties.getConfigKeys().getEnableMagicLinkLogin(), "AUTHENTICATION",
                        false),
                new ConfigKeyRecord(InstanceConfigurationKey.GOOGLE_CLIENT_ID,
                        appProperties.getConfigKeys().getGoogleClientId(), "GOOGLE", false),
                new ConfigKeyRecord(InstanceConfigurationKey.GOOGLE_CLIENT_SECRET,
                        appProperties.getConfigKeys().getGoogleClientSecret(), "GOOGLE", true),
                new ConfigKeyRecord(InstanceConfigurationKey.GITHUB_CLIENT_ID,
                        appProperties.getConfigKeys().getGithubClientId(), "GITHUB", false),
                new ConfigKeyRecord(InstanceConfigurationKey.GITHUB_CLIENT_SECRET,
                        appProperties.getConfigKeys().getGithubClientSecret(), "GITHUB", true),
                new ConfigKeyRecord(InstanceConfigurationKey.GITLAB_HOST,
                        appProperties.getConfigKeys().getGitlabHost(), "GITLAB", false),
                new ConfigKeyRecord(InstanceConfigurationKey.GITLAB_CLIENT_ID,
                        appProperties.getConfigKeys().getGitlabClientId(), "GITLAB", false),
                new ConfigKeyRecord(InstanceConfigurationKey.GITLAB_CLIENT_SECRET,
                        appProperties.getConfigKeys().getGitlabClientSecret(), "GITLAB", true));
    }

    public Map<InstanceConfigurationKey, String> getConfigurationValues(
            final List<ConfigKeyLookupRecord> keys) {
        Map<InstanceConfigurationKey, InstanceConfiguration> dbConfig =
                iConfigurationRepository.findAll().stream().collect(
                        Collectors.toMap(InstanceConfiguration::getKey, Function.identity()));
        return getConfigurationValuesFromCache(keys, dbConfig);
    }

    public Map<InstanceConfigurationKey, String> getConfigurationValuesFromCache(
            final List<ConfigKeyLookupRecord> keys,
            final Map<InstanceConfigurationKey, InstanceConfiguration> dbConfig) {
        Map<InstanceConfigurationKey, String> result = new HashMap<>();
        if (appProperties.isSkipEnvVar()) {
            for (ConfigKeyLookupRecord key : keys) {
                InstanceConfiguration config = dbConfig.get(key.key());
                if (config != null) {
                    String value =
                            config.isEncrypted() ? decrypt(config.getValue()) : config.getValue();
                    result.put(key.key(), value);
                } else {
                    result.put(key.key(), key.defaultValue());
                }
            }
        } else {
            for (ConfigKeyLookupRecord key : keys) {
                result.put(key.key(), key.defaultValue());
            }
        }

        return result;
    }

    public List<InstanceConfiguration> loadMissingConfigKeys(final List<ConfigKeyRecord> records,
            final Set<InstanceConfigurationKey> existingKeys) {
        List<InstanceConfiguration> payload = new ArrayList<>();
        // insert missing config keys
        for (ConfigKeyRecord record : records) {
            if (existingKeys.contains(record.key())) {
                log.warn("{} configuration already exists", record.key());
                continue;
            }

            payload.add(newConfig(record.key(), record.value(), record.category(),
                    record.isEncrypted()));

            log.info("{} loaded with value from environment or application.yaml", record.key());
        }
        return payload;
    }

    public List<InstanceConfiguration> loadIntegrationFlags(
            final Map<InstanceConfigurationKey, InstanceConfiguration> dbConfig) {
        List<InstanceConfiguration> payload = new ArrayList<>();
        payload.add(loadGoogleFlag(dbConfig));
        payload.add(loadGithubFlag(dbConfig));
        payload.add(loadGitlabFlag(dbConfig));

        return payload;
    }

    public String encryptValue(String value) {
        return encrypt(value);
    }

    private InstanceConfiguration loadGoogleFlag(
            Map<InstanceConfigurationKey, InstanceConfiguration> dbConfig) {
        // GOOGLE
        Map<InstanceConfigurationKey, String> google = getConfigurationValuesFromCache(
                List.of(new ConfigKeyLookupRecord(InstanceConfigurationKey.GOOGLE_CLIENT_ID,
                        appProperties.getConfigKeys().getGoogleClientId())),
                dbConfig);
        String isEnabled = isNonEmpty(google.get(InstanceConfigurationKey.GOOGLE_CLIENT_ID))
                && isNonEmpty(google.get(InstanceConfigurationKey.GOOGLE_CLIENT_SECRET)) ? "1"
                        : "0";
        return newConfig(InstanceConfigurationKey.IS_GOOGLE_ENABLED, isEnabled, "AUTHENTICATION",
                false);
    }

    private InstanceConfiguration loadGithubFlag(
            Map<InstanceConfigurationKey, InstanceConfiguration> dbConfig) {
        // GITHUB
        Map<InstanceConfigurationKey, String> github = getConfigurationValuesFromCache(List.of(
                new ConfigKeyLookupRecord(InstanceConfigurationKey.GITHUB_CLIENT_ID,
                        appProperties.getConfigKeys().getGithubClientId()),
                new ConfigKeyLookupRecord(InstanceConfigurationKey.GITHUB_CLIENT_SECRET,
                        appProperties.getConfigKeys().getGithubClientSecret())),
                dbConfig);
        String isEnabled = isNonEmpty(github.get(InstanceConfigurationKey.GITHUB_CLIENT_ID))
                && isNonEmpty(github.get(InstanceConfigurationKey.GITHUB_CLIENT_SECRET)) ? "1"
                        : "0";

        return newConfig(InstanceConfigurationKey.IS_GITHUB_ENABLED, isEnabled, "AUTHENTICATION",
                false);
    }

    private InstanceConfiguration loadGitlabFlag(
            Map<InstanceConfigurationKey, InstanceConfiguration> dbConfig) {
        // GITLAB
        Map<InstanceConfigurationKey, String> gitlab = getConfigurationValuesFromCache(List.of(
                new ConfigKeyLookupRecord(InstanceConfigurationKey.GITLAB_HOST,
                        appProperties.getConfigKeys().getGitlabHost()),
                new ConfigKeyLookupRecord(InstanceConfigurationKey.GITLAB_CLIENT_ID,
                        appProperties.getConfigKeys().getGitlabClientId()),
                new ConfigKeyLookupRecord(InstanceConfigurationKey.GITLAB_CLIENT_SECRET,
                        appProperties.getConfigKeys().getGitlabClientSecret())),
                dbConfig);
        String isEnabled = isNonEmpty(gitlab.get(InstanceConfigurationKey.GITLAB_HOST))
                && isNonEmpty(gitlab.get(InstanceConfigurationKey.GITLAB_CLIENT_ID))
                && isNonEmpty(gitlab.get(InstanceConfigurationKey.GITLAB_CLIENT_SECRET)) ? "1"
                        : "0";
        return newConfig(InstanceConfigurationKey.IS_GITLAB_ENABLED, isEnabled, "AUTHENTICATION",
                false);
    }

    private InstanceConfiguration newConfig(InstanceConfigurationKey key, String value,
            String category, boolean isEncrypted) {
        InstanceConfiguration config = new InstanceConfiguration();
        config.setKey(key);
        config.setCategory(category);
        config.setEncrypted(isEncrypted);
        config.setValue(isEncrypted ? encrypt(value) : value);
        return config;
    }

    private String encrypt(String rawValue) {
        return isNonEmpty(rawValue) ? encryptor.encrypt(rawValue) : "";
    }

    private String decrypt(String input) {
        return isNonEmpty(input) ? encryptor.decrypt(input) : "";
    }

    public boolean isNonEmpty(String val) {
        return val != null && !val.isBlank();
    }

}
