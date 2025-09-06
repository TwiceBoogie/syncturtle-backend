package dev.twiceb.common.dto.response;

import java.util.Map;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConfigResponse {
    Map<InstanceConfigurationKey, String> values;
    long version;
}
