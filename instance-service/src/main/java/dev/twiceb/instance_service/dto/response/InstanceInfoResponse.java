package dev.twiceb.instance_service.dto.response;

import java.util.UUID;
import dev.twiceb.instance_service.enums.InstanceEdition;
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
