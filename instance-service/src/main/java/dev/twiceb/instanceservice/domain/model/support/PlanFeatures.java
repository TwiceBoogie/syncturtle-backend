package dev.twiceb.instanceservice.domain.model.support;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanFeatures {
    private String description;
    private Limits limits;
    private Toggles features;
    private List<String> labels;

    @Getter
    @Builder
    @Jacksonized
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Limits {
        private int workspaces;
        private int membersPerWorkspace;
        private int projectsPerWorkspace;
        private int storageGb;
        private int automationsPerWorkspace;
    }

    @Getter
    @Builder
    @Jacksonized
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Toggles {
        private boolean customDomains;
        private boolean aiAssist;
        private boolean webhooks;
        private boolean apiAccess;
        private boolean prioritySupport;
        private boolean auditLogs;
        private int retentionDays;
        private boolean ssoSaml;
        private boolean scim;
        private boolean backupsDaily;
        private String sla;
    }
}
