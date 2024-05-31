package dev.twiceb.passwordservice.dto.response;

import lombok.Data;

@Data
public class EntropyResponse {
    private double passwordComplexityScore;
}
