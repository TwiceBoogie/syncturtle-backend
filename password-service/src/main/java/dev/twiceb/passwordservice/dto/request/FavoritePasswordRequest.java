package dev.twiceb.passwordservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavoritePasswordRequest {

    @NotNull(message = "Value must be a boolean.")
    private boolean favorite;
}
