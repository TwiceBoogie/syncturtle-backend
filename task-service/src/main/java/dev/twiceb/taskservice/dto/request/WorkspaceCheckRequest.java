package dev.twiceb.taskservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WorkspaceCheckRequest {
    @NotBlank
    @Max(value = 48)
    private String slug;
}
