package dev.twiceb.passwordservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateTagsOnPasswordRequest {
    @NotNull
    private List<Long> categories = new ArrayList<>();
}
