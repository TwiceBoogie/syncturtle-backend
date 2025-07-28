package dev.twiceb.instance_service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private boolean skipEnvVar;
    private boolean test;
    private String edition;
    private String webUrl;
    private String name;
    private String secretKey;
    private BaseUrls baseUrls;
    private ConfigKeys configKeys;

    @Data
    public static class ConfigKeys {
        private String enableSignup;
        private String enableEmailPassword;
        private String enableMagicLinkLogin;
        private String googleClientId;
        private String googleClientSecret;
        private String githubClientId;
        private String githubClientSecret;
        private String gitlabHost;
        private String gitlabClientId;
        private String gitlabClientSecret;
    }

    @Data
    public static class BaseUrls {
        private String admin;
        private String app;
    }
}
