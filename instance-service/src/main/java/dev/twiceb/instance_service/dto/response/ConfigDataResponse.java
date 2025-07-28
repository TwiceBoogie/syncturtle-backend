package dev.twiceb.instance_service.dto.response;

import java.util.Map;
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

    public static ConfigDataResponse fromConfigMap(Map<String, String> config) {
        ConfigDataResponse response = new ConfigDataResponse();
        response.setEnableSignup(isTrue(config.get("ENABLE_SIGNUP")));
        response.setGoogleEnabled(isTrue(config.get("IS_GOOGLE_ENABLED")));
        response.setGithubEnabled(isTrue(config.get("IS_GITHUB_ENABLED")));
        response.setGithubAppName(config.get("GITHUB_APP_NAME"));
        response.setGitlabEnabled(isTrue(config.get("IS_GITLAB_ENABLED")));
        response.setMagicLinkLoginEnabled(isTrue(config.get("ENABLE_MAGIC_LINK_LOGIN")));
        response.setEmailPasswordEnabled(isTrue(config.get("ENABLE_EMAIL_PASSWORD")));
        return response;
    }

    private static boolean isTrue(String val) {
        return "1".equals(val);
    }
}
