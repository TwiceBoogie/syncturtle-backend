package dev.twiceb.instanceservice.dto.request;

import lombok.Data;

@Data
public class InstanceConfigurationUpdateRequest {
    private String ENABLE_SIGNUP;
    private String ENABLE_MAGIC_LINK_LOGIN;
    private String ENABLE_EMAIL_PASSWORD;
    private String IS_GOOGLE_ENABLED;
    private String IS_GITHUB_ENABLED;
    private String IS_GITLAB_ENABLED;
    private String GITHUB_APP_NAME;
    private String EMAIL_HOST;
}
