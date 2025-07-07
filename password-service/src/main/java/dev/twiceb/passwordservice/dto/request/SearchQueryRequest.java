package dev.twiceb.passwordservice.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SearchQueryRequest {

    @Pattern(regexp = "^[a-zA-Z0-9-_.]*$", message = "Invalid characters. Alphanumeric, hyphens, and underscores are allowed.")
    @Pattern(regexp = "^[^<>&]*$", message = "HTML tags are not allowed in notes")
    private String searchQuery;
}
