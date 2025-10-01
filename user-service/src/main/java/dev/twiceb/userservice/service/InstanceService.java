package dev.twiceb.userservice.service;

import dev.twiceb.common.dto.response.InstanceStatusResult;

public interface InstanceService {
    boolean isSetupDone();

    InstanceStatusResult status();
}
