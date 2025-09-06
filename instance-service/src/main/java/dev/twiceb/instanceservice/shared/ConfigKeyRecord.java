package dev.twiceb.instanceservice.shared;

import dev.twiceb.common.enums.InstanceConfigurationKey;

public record ConfigKeyRecord(InstanceConfigurationKey key, String value, String category,
        boolean isEncrypted) {
}
