package dev.twiceb.instanceservice.service.util;

import java.util.List;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import dev.twiceb.instanceservice.domain.model.InstanceConfiguration;
import dev.twiceb.instanceservice.domain.repository.InstanceConfigurationRepository;
import dev.twiceb.instanceservice.service.util.AppProperties.ConfigKeys;
import lombok.RequiredArgsConstructor;
import lombok.Value;
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

    public void ensureMandatorySecretsPresentOrThrow() {
        if (!StringUtils.hasText(appProperties.getSecretKey())) {
            throw new IllegalStateException("SECRET_KEY is required (application.yaml or env)");
        }
    }

    public InsertPayload buildInsertPayload(InstanceConfigurationKey key) {
        final String category = categoryFor(key);
        final boolean encrypted = isEncrypted(key);
        final String raw = appValueOrDefault(key);
        final String toStore = encrypted ? safeEncrypt(raw) : nonNull(raw);
        return new InsertPayload(category, encrypted, toStore);
    }

    public String resolveValue(InstanceConfigurationKey key) {
        if (appProperties.isSkipEnvVar()) {
            InstanceConfiguration row = iConfigurationRepository.findByKey(key).orElse(null);
            if (row != null) {
                return row.isEncrypted() ? safeDecrypt(row.getValue()) : nonNull(row.getValue());
            }
            return defaultFor(key); // db missing
        }
        return appValueOrDefault(key); // application.yaml
    }

    public void insertPlainFlag(InstanceConfigurationKey key, String value, String category) {
        if (iConfigurationRepository.existsByKey(key)) {
            return;
        }

        InstanceConfiguration ic = new InstanceConfiguration();
        ic.setKey(key);
        ic.setValue(nonNull(value));
        ic.setCategory(nonNull(category));
        ic.setEncrypted(false);
        try {
            iConfigurationRepository.save(ic);
        } catch (DataIntegrityViolationException e) {
            // ignored
        }
    }

    public EmailConfig getEmailConfiguration() {
        String host = resolveValue(InstanceConfigurationKey.EMAIL_HOST);
        String user = resolveValue(InstanceConfigurationKey.EMAIL_HOST_USER);
        String password = resolveValue(InstanceConfigurationKey.EMAIL_HOST_PASSWORD);
        int port = parseIntOr(resolveValue(InstanceConfigurationKey.EMAIL_PORT), 587);
        boolean tls = "1".equals(resolveValue(InstanceConfigurationKey.EMAIL_USE_TLS));
        boolean ssl = "1".equals(resolveValue(InstanceConfigurationKey.EMAIL_USE_SSL));
        String from = orDefault(resolveValue(InstanceConfigurationKey.EMAIL_FROM),
                "Team Syncturtle <team@mailer.syncturtle.so");

        return new EmailConfig(host, user, password, port, tls, ssl, from);
    }

    private String categoryFor(InstanceConfigurationKey key) {
        switch (key) {
            // auth / workspace
            case ENABLE_SIGNUP, ENABLE_EMAIL_PASSWORD, ENABLE_MAGIC_LINK_LOGIN -> {
                return "AUTHENTICATION";
            }
            // smtp
            case ENABLE_SMTP, EMAIL_HOST, EMAIL_HOST_USER, EMAIL_HOST_PASSWORD, EMAIL_PORT, EMAIL_FROM, EMAIL_USE_TLS, EMAIL_USE_SSL -> {
                return "SMTP";
            }

            // oauth
            case GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET -> {
                return "GOOGLE";
            }
            case GITHUB_CLIENT_ID, GITHUB_CLIENT_SECRET, GITHUB_APP_NAME -> {
                return "GITHUB";
            }
            case GITLAB_HOST, GITLAB_CLIENT_ID, GITLAB_CLIENT_SECRET -> {
                return "GITLAB";
            }

            // derived flags
            case IS_GOOGLE_ENABLED, IS_GITHUB_ENABLED, IS_GITLAB_ENABLED -> {
                return "AUTHENTICATION";
            }

            default -> {
                return "MISC";
            }
        }
    }

    private boolean isEncrypted(InstanceConfigurationKey key) {
        switch (key) {
            case EMAIL_HOST_PASSWORD, GOOGLE_CLIENT_SECRET, GITHUB_CLIENT_SECRET, GITLAB_CLIENT_SECRET:
                return true;
            default:
                return false;
        }
    }

    // defaults used when db is empty and skipEnvVar = true or appProperties is blanks
    private String defaultFor(InstanceConfigurationKey key) {
        switch (key) {
            // auth / workspace
            case ENABLE_SIGNUP:
                return "1";
            case ENABLE_EMAIL_PASSWORD:
                return "1";
            case ENABLE_MAGIC_LINK_LOGIN:
                return "0";

            // smtp
            case ENABLE_SMTP:
                return "0";
            case EMAIL_HOST:
                return "";
            case EMAIL_HOST_USER:
                return "";
            case EMAIL_HOST_PASSWORD:
                return "";
            case EMAIL_PORT:
                return "587";
            case EMAIL_FROM:
                return "";
            case EMAIL_USE_TLS:
                return "1";
            case EMAIL_USE_SSL:
                return "0";

            // oauth
            case GOOGLE_CLIENT_ID:
                return "";
            case GOOGLE_CLIENT_SECRET:
                return "";
            case GITHUB_CLIENT_ID:
                return "";
            case GITHUB_CLIENT_SECRET:
                return "";
            case GITHUB_APP_NAME:
                return "";
            case GITLAB_HOST:
                return "https://gitlab.com";
            case GITLAB_CLIENT_ID:
                return "";
            case GITLAB_CLIENT_SECRET:
                return "";

            // derived flags (safe fallback)
            case IS_GOOGLE_ENABLED, IS_GITHUB_ENABLED, IS_GITLAB_ENABLED:
                return "0";
            default:
                return "";
        }
    }

    private String appValueOrDefault(InstanceConfigurationKey key) {
        ConfigKeys c = appProperties.getConfigKeys();
        switch (key) {
            // auth / workspace
            case ENABLE_SIGNUP:
                return orDefault(c.getEnableSignup(), defaultFor(key));
            case ENABLE_EMAIL_PASSWORD:
                return orDefault(c.getEnableEmailPassword(), defaultFor(key));
            case ENABLE_MAGIC_LINK_LOGIN:
                return orDefault(c.getEnableMagicLinkLogin(), defaultFor(key));

            // smtp
            case ENABLE_SMTP:
                return orDefault(c.getEnableSmtp(), defaultFor(key));
            case EMAIL_HOST:
                return orDefault(c.getEmailHost(), defaultFor(key));
            case EMAIL_HOST_USER:
                return orDefault(c.getEmailHostUser(), defaultFor(key));
            case EMAIL_HOST_PASSWORD:
                return orDefault(c.getEmailHostPassword(), defaultFor(key));
            case EMAIL_PORT:
                return orDefault(c.getEmailPort(), defaultFor(key));
            case EMAIL_FROM:
                return orDefault(c.getEmailFrom(), defaultFor(key));
            case EMAIL_USE_TLS:
                return orDefault(c.getEmailUseTls(), defaultFor(key));
            case EMAIL_USE_SSL:
                return orDefault(c.getEmailUseSsl(), defaultFor(key));

            // oauth
            case GOOGLE_CLIENT_ID:
                return orDefault(c.getGoogleClientId(), defaultFor(key));
            case GOOGLE_CLIENT_SECRET:
                return orDefault(c.getGoogleClientSecret(), defaultFor(key));
            case GITHUB_CLIENT_ID:
                return orDefault(c.getGithubClientId(), defaultFor(key));
            case GITHUB_CLIENT_SECRET:
                return orDefault(c.getGithubClientSecret(), defaultFor(key));
            case GITLAB_HOST:
                return orDefault(c.getGitlabHost(), defaultFor(key));
            case GITLAB_CLIENT_ID:
                return orDefault(c.getGitlabClientId(), defaultFor(key));
            case GITLAB_CLIENT_SECRET:
                return orDefault(c.getGitlabClientSecret(), defaultFor(key));

            // derived flags
            case IS_GOOGLE_ENABLED, IS_GITHUB_ENABLED, IS_GITLAB_ENABLED:
                return defaultFor(key);

            default:
                return defaultFor(key);
        }
    }

    public boolean nonEmpty(String v) {
        return v != null && !v.isBlank();
    }

    public static int parseIntOr(String value, int d) {
        try {
            return Integer.parseInt(nonNull(value).trim());
        } catch (Exception e) {
            return d;
        }
    }

    public String decryptIfNeeded(InstanceConfiguration ic) {
        String value = ic.getValue();
        return ic.isEncrypted() ? safeDecrypt(value) : value;
    }

    public void encryptIfNeeded(InstanceConfiguration ic, String value) {
        ic.setValue(ic.isEncrypted() ? safeEncrypt(value) : value);
    }

    private String safeEncrypt(String raw) {
        return nonEmpty(raw) ? encryptor.encrypt(raw) : "";
    }

    private String safeDecrypt(String value) {
        return nonEmpty(value) ? encryptor.decrypt(value) : "";
    }

    private static String nonNull(String v) {
        return v == null ? "" : v;
    }

    private static String orDefault(String v, String d) {
        return StringUtils.hasText(v) ? v : d;
    }

    @Value
    public static class InsertPayload {
        String category;
        boolean encrypted;
        String value;
    }

    @Value
    public static class EmailConfig {
        String host;
        String user;
        String password;
        int port;
        boolean useTls;
        boolean useSSl;
        String from;
    }

}
