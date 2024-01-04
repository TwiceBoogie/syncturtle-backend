package dev.twiceb.taskservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class TagsRequest {
    @NotNull(message = "Tag/s name must be a valid value.")
    @Size(max = 10, message = "Cannot exceed more than 10.")
    @Pattern(regexp = "^\\w+(?:-\\w+)*$", message = "Invalid tag name format.")
    private Set<String> tagNames;
}
