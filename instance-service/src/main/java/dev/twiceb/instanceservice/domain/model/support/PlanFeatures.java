package dev.twiceb.instanceservice.domain.model.support;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlanFeatures {
    private String description;
    private Limits limits;
    private Toggles features;

    @Getter
    @Builder
    public static class Limits {
        private int workspaces;
        private int membersPerWorkspace;
        private int projectsPerWorkspace;
        private int storageGb;
        private int automationsPerWorkspace;
    }

    @Getter
    @Builder
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
