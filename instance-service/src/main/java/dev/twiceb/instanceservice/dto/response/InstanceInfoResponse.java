package dev.twiceb.instanceservice.dto.response;

import java.time.Instant;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.twiceb.common.enums.InstanceEdition;
import lombok.Data;

@Data
public class InstanceInfoResponse {
    private UUID id;
    private String slug;
    private String instanceName;
    private String currentVersion;
    private String latestVersion;
    private InstanceEdition edition;
    private String domain;
    private Instant lastCheckedAt;
    private String namespace;
    @JsonProperty("isSetupDone")
    private boolean setupDone;
    @JsonProperty("isVerified")
    private boolean verified;
    @JsonProperty("isTest")
    private boolean test;
    private Instant createdAt;
    private Instant updatedAt;
}
