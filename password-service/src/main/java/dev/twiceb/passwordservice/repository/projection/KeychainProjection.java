package dev.twiceb.passwordservice.repository.projection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import dev.twiceb.passwordservice.enums.DomainStatus;

public interface KeychainProjection {
    UUID getId();
    String getUsername();
    String getDomain();
    String getWebsiteUrl();
    boolean getFavorite();
    String getFakePassword();
    LocalDate getExpiryDate();
    DomainStatus getStatus();
    LocalDateTime getCreatedAt();
    String getNotes();
    EntropyProjection getComplexityMetric();
    List<CategoryProjection> getCategories();
}
