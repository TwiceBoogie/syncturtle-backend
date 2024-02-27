package dev.twiceb.passwordservice.dto.response;

import lombok.Data;

@Data
public class PasswordVaultHealthResponse {
    private int totalPasswords;
    private int totalWeakPasswords;
    private int totalPasswordReuseCount;
    private double averageEntropyScore;
    private double vaultHealthPercentage;
}
