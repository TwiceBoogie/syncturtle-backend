package dev.twiceb.instance_service.dto.response;

import lombok.Data;

@Data
public class InstanceSetupResponse {
    private boolean activated;
    private boolean setupDone;
    private boolean workspacesExist;
    private ConfigDataResponse config;
    private InstanceInfoResponse instance;

    public InstanceSetupResponse(boolean activated, boolean setupDone) {
        this.activated = activated;
        this.setupDone = setupDone;
    }
}
