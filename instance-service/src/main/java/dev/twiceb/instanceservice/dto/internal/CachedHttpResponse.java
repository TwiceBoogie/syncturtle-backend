package dev.twiceb.instanceservice.dto.internal;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CachedHttpResponse {
    private int status;
    private Map<String, String> headers;
    byte[] body;
}
