package dev.twiceb.passwordservice.service.util;

import dev.twiceb.passwordservice.model.PasswordComplexityMetric;
import org.springframework.stereotype.Component;

public class PasswordComplexityAnalyzer {

    public static PasswordComplexityMetric analyzePassword(String password) {
        int length = password.length();
        int characterTypesUsed = countCharacterTypes(password);
        double entropy = calculateEntropy(password);

        return new PasswordComplexityMetric(
                length,
                characterTypesUsed,
                entropy
        );
    }

    private static double calculateEntropy(String password) {
        int characterSetSize = 92;
        double combinations = Math.pow(characterSetSize, password.length());
        return Math.log(combinations) / Math.log(2);
    }

    private static int countCharacterTypes(String password) {
        int uppercase = 0;
        int lowercase = 0;
        int numbers = 0;
        int specialChar = 0;

        for (int i = 0; i < password.length(); i++) {
            int ascii = password.charAt(i);
            if (ascii >= 65 && ascii <= 90) uppercase = 1;
            if (ascii >= 97 && ascii <= 122) lowercase = 1;
            if (ascii >= 48 && ascii <= 57) numbers = 1;
            if ((ascii >= 33 && ascii <= 47) || (ascii >= 58 && ascii <= 64)) specialChar = 1;
        }

        return uppercase + lowercase + numbers + specialChar;
    }
}
