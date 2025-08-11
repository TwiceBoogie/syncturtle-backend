package dev.twiceb.common.dto.response;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConfigResponse {
    Map<String, String> values;
    long version;
}
