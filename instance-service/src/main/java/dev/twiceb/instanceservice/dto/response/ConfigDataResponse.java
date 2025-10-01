package dev.twiceb.instanceservice.dto.response;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import lombok.Data;

@Data
public class ConfigDataResponse {
    private boolean enableSignup;
    // private boolean workspaceCreationDisabled;
    @JsonProperty("isGoogleEnabled")
    private boolean googleEnabled;
    @JsonProperty("isGithubEnabled")
    private boolean githubEnabled;
    @JsonProperty("isGitlabEnabled")
    private boolean gitlabEnabled;
    @JsonProperty("isMagicLoginEnabled")
    private boolean magicLinkLoginEnabled;
    @JsonProperty("isEmailPasswordEnabled")
    private boolean emailPasswordEnabled;
    private String githubAppName;
    // private String slackClientId;
    // private String posthogApiKey;
    // private String posthogHost;
    // private boolean unsplashConfigured;
    // private boolean openaiConfigured;
    @JsonProperty("isSmtpConfigured")
    private boolean smtpConfigured;
    private String adminBaseUrl;
    private String appBaseUrl;

    public static ConfigDataResponse fromConfigMap(Map<InstanceConfigurationKey, String> config,
            boolean isSmtpConfigured, String adminBaseUrl, String appBaseUrl) {
        ConfigDataResponse response = new ConfigDataResponse();
        response.setEnableSignup(isTrue(config.get(InstanceConfigurationKey.ENABLE_SIGNUP)));
        response.setGoogleEnabled(isTrue(config.get(InstanceConfigurationKey.IS_GOOGLE_ENABLED)));
        response.setGithubEnabled(isTrue(config.get(InstanceConfigurationKey.IS_GITHUB_ENABLED)));
        response.setGithubAppName(config.get(InstanceConfigurationKey.GITHUB_APP_NAME));
        response.setGitlabEnabled(isTrue(config.get(InstanceConfigurationKey.IS_GITLAB_ENABLED)));
        response.setMagicLinkLoginEnabled(
                isTrue(config.get(InstanceConfigurationKey.ENABLE_MAGIC_LINK_LOGIN)));
        response.setEmailPasswordEnabled(
                isTrue(config.get(InstanceConfigurationKey.ENABLE_EMAIL_PASSWORD)));
        response.setSmtpConfigured(isSmtpConfigured);
        response.setAdminBaseUrl(adminBaseUrl);
        response.setAppBaseUrl(appBaseUrl);
        return response;
    }

    private static boolean isTrue(String val) {
        return "1".equals(val);
    }
}
