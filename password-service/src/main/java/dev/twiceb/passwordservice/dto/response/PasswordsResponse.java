package dev.twiceb.passwordservice.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import dev.twiceb.passwordservice.enums.DomainStatus;
import lombok.Data;

@Data
public class PasswordsResponse {
    private UUID id;
    private String username;
    private String domain;
    private String websiteUrl;
    private boolean favorite;
    private String fakePassword;
    private LocalDate expiryDate;
    private DomainStatus status;
    private LocalDateTime createdAt;
    private String notes;
    private EntropyResponse complexityMetric;
    private List<CategoryListResponse> categories;
}
