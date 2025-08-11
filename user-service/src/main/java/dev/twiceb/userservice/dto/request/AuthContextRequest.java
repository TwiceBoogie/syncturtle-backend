package dev.twiceb.userservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthContextRequest<T> {
    private MetadataDto metadata;
    private T payload;
}
