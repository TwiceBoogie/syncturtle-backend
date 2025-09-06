package dev.twiceb.instanceservice.dto.response;

import java.util.UUID;
import dev.twiceb.instanceservice.domain.enums.InstanceEdition;
import lombok.Data;

@Data
public class InstanceInfoResponse {
    private UUID id;
    private String slug;
    private String name;
    private InstanceEdition edition;
    private String currentVersion;
    private String domain;
    private String namespace;
    private boolean setupDone;
    private boolean verified;
    private boolean test;
}
