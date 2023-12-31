package dev.twiceb.taskservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TagsRequest {
    @NotNull(message = "Tag/s name must be a valid value.")
    @Size(max = 10, message = "Cannot exceed more than 10.")
    private String[] tagName;
}
