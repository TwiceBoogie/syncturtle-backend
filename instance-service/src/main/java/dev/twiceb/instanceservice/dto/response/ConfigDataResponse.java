package dev.twiceb.instanceservice.dto.response;

import java.util.Map;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import lombok.Data;

@Data
public class ConfigDataResponse {
    private boolean enableSignup;
    // private boolean workspaceCreationDisabled;
    private boolean googleEnabled;
    private boolean githubEnabled;
    private boolean gitlabEnabled;
    private boolean magicLinkLoginEnabled;
    private boolean emailPasswordEnabled;
    private String githubAppName;
    // private String slackClientId;
    // private String posthogApiKey;
    // private String posthogHost;
    // private boolean unsplashConfigured;
    // private boolean openaiConfigured;

    public static ConfigDataResponse fromConfigMap(Map<InstanceConfigurationKey, String> config) {
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
        return response;
    }

    private static boolean isTrue(String val) {
        return "1".equals(val);
    }
}
