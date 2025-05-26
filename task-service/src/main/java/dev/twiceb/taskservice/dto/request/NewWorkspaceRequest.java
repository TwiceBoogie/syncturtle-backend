package dev.twiceb.taskservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewWorkspaceRequest {

    @NotNull
    @Max(value = 80)
    private String name;
    private String url;
    private String logo;
    private String slug;
}
