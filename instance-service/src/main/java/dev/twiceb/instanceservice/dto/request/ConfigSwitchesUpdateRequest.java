package dev.twiceb.instanceservice.dto.request;

import lombok.Data;

@Data
public class ConfigSwitchesUpdateRequest {
    private Boolean enabledSignUp;
    private Boolean googleEnabled;
    private Boolean githubEnabled;
    private String githubAppName;
    private String gitlabEnabled;
    private String emailHost;
    private Boolean magicLinkLoginEnabled;
    private Boolean emailPasswordEnabled;
}
