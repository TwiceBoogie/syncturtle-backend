package dev.twiceb.common.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthContextRequest<T> {
    private RequestMetadata metadata;
    private T payload;
}
