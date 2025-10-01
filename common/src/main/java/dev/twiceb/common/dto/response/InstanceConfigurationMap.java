package dev.twiceb.common.dto.response;

import java.util.Map;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InstanceConfigurationMap {
    private Map<InstanceConfigurationKey, String> values;
}
