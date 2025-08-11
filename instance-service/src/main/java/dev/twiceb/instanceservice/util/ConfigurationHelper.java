package dev.twiceb.instanceservice.util;

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
import dev.twiceb.instanceservice.AppProperties;
import dev.twiceb.instanceservice.ConfigKeyLookupRecord;
import dev.twiceb.instanceservice.ConfigKeyRecord;
import dev.twiceb.instanceservice.model.InstanceConfiguration;
import dev.twiceb.instanceservice.repository.InstanceConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfigurationHelper {

    public static final List<String> INTEGRATION_FLAGS =
            List.of("IS_GOOGLE_ENABLED", "IS_GITHUB_ENABLED", "IS_GITLAB_ENABLED");

    @Qualifier("encryptorBean")
    private final StringEncryptor encryptor;
    private final InstanceConfigurationRepository iConfigurationRepository;
    private final AppProperties appProperties;

    public List<ConfigKeyRecord> loadEnvConfigKeys() {
        List<ConfigKeyRecord> mandatoryConfig = List.of(
                new ConfigKeyRecord("SECRET_KEY", appProperties.getSecretKey(), "SECURITY", true));

        for (ConfigKeyRecord config : mandatoryConfig) {
            if (!isNonEmpty(config.value())) {
                throw new IllegalStateException(
                        config.key() + " config value is required but not set.");
            }
        }

        return List.of(
                new ConfigKeyRecord("ENABLE_SIGNUP",
                        appProperties.getConfigKeys().getEnableSignup(), "AUTHENTICATION", false),
                new ConfigKeyRecord("ENABLE_EMAIL_PASSWORD",
                        appProperties.getConfigKeys().getEnableEmailPassword(), "AUTHENTICATION",
                        false),
                new ConfigKeyRecord("ENABLE_MAGIC_LINK_LOGIN",
                        appProperties.getConfigKeys().getEnableMagicLinkLogin(), "AUTHENTICATION",
                        false),
                new ConfigKeyRecord("GOOGLE_CLIENT_ID",
                        appProperties.getConfigKeys().getGoogleClientId(), "GOOGLE", false),
                new ConfigKeyRecord("GOOGLE_CLIENT_SECRET",
                        appProperties.getConfigKeys().getGoogleClientSecret(), "GOOGLE", true),
                new ConfigKeyRecord("GITHUB_CLIENT_ID",
                        appProperties.getConfigKeys().getGithubClientId(), "GITHUB", false),
                new ConfigKeyRecord("GITHUB_CLIENT_SECRET",
                        appProperties.getConfigKeys().getGithubClientSecret(), "GITHUB", true),
                new ConfigKeyRecord("GITLAB_HOST", appProperties.getConfigKeys().getGitlabHost(),
                        "GITLAB", false),
                new ConfigKeyRecord("GITLAB_CLIENT_ID",
                        appProperties.getConfigKeys().getGitlabClientId(), "GITLAB", false),
                new ConfigKeyRecord("GITLAB_CLIENT_SECRET",
                        appProperties.getConfigKeys().getGitlabClientSecret(), "GITLAB", true));
    }

    public Map<String, String> getConfigurationValues(final List<ConfigKeyLookupRecord> keys) {
        Map<String, InstanceConfiguration> dbConfig = iConfigurationRepository.findAll().stream()
                .collect(Collectors.toMap(InstanceConfiguration::getKey, Function.identity()));
        return getConfigurationValuesFromCache(keys, dbConfig);
    }

    public Map<String, String> getConfigurationValuesFromCache(
            final List<ConfigKeyLookupRecord> keys,
            final Map<String, InstanceConfiguration> dbConfig) {
        Map<String, String> result = new HashMap<>();
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
            final Set<String> existingKeys) {
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
            final Map<String, InstanceConfiguration> dbConfig) {
        List<InstanceConfiguration> payload = new ArrayList<>();
        payload.add(loadGoogleFlag(dbConfig));
        payload.add(loadGithubFlag(dbConfig));
        payload.add(loadGitlabFlag(dbConfig));

        return payload;
    }

    private InstanceConfiguration loadGoogleFlag(Map<String, InstanceConfiguration> dbConfig) {
        // GOOGLE
        Map<String, String> google = getConfigurationValuesFromCache(
                List.of(new ConfigKeyLookupRecord("GOOGLE_CLIENT_ID",
                        appProperties.getConfigKeys().getGoogleClientId())),
                dbConfig);
        String isEnabled = isNonEmpty(google.get("GOOGLE_CLIENT_ID"))
                && isNonEmpty(google.get("GOOGLE_CLIENT_SECRET")) ? "1" : "0";
        return newConfig("IS_GOOGLE_ENABLED", isEnabled, "AUTHENTICATION", false);
    }

    private InstanceConfiguration loadGithubFlag(Map<String, InstanceConfiguration> dbConfig) {
        // GITHUB
        Map<String, String> github = getConfigurationValuesFromCache(List.of(
                new ConfigKeyLookupRecord("GITHUB_CLIENT_ID",
                        appProperties.getConfigKeys().getGithubClientId()),
                new ConfigKeyLookupRecord("GITHUB_CLIENT_SECRET",
                        appProperties.getConfigKeys().getGithubClientSecret())),
                dbConfig);
        String isEnabled = isNonEmpty(github.get("GITHUB_CLIENT_ID"))
                && isNonEmpty(github.get("GITHUB_CLIENT_SECRET")) ? "1" : "0";

        return newConfig("IS_GITHUB_ENABLED", isEnabled, "AUTHENTICATION", false);
    }

    private InstanceConfiguration loadGitlabFlag(Map<String, InstanceConfiguration> dbConfig) {
        // GITLAB
        Map<String, String> gitlab = getConfigurationValuesFromCache(List.of(
                new ConfigKeyLookupRecord("GITLAB_HOST",
                        appProperties.getConfigKeys().getGitlabHost()),
                new ConfigKeyLookupRecord("GITLAB_CLIENT_ID",
                        appProperties.getConfigKeys().getGitlabClientId()),
                new ConfigKeyLookupRecord("GITLAB_CLIENT_SECRET",
                        appProperties.getConfigKeys().getGitlabClientSecret())),
                dbConfig);
        String isEnabled =
                isNonEmpty(gitlab.get("GITLAB_HOST")) && isNonEmpty(gitlab.get("GITLAB_CLIENT_ID"))
                        && isNonEmpty(gitlab.get("GITLAB_CLIENT_SECRET")) ? "1" : "0";
        return newConfig("IS_GITLAB_ENABLED", isEnabled, "AUTHENTICATION", false);
    }

    private InstanceConfiguration newConfig(String key, String value, String category,
            boolean isEncrypted) {
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
