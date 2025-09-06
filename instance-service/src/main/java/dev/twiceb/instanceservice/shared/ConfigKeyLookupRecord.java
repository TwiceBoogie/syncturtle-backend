package dev.twiceb.instanceservice.shared;

import dev.twiceb.common.enums.InstanceConfigurationKey;

public record ConfigKeyLookupRecord(InstanceConfigurationKey key, String defaultValue) {

}
