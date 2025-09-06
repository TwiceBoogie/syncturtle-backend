package dev.twiceb.common.dto.response;

import java.time.Instant;
import lombok.Data;

@Data
public class AccessTokenResponse {
    private String jwt;
    private Instant exp;
}
