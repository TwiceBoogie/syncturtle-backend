package dev.twiceb.passwordservice.service.util;

import dev.twiceb.passwordservice.model.PasswordComplexityMetric;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class PasswordComplexityAnalyzer {

    private static final int MAX_PASSWORD_LENGTH = 40;
    private static final int MAX_CHARACTER_TYPES = 4;

    public static PasswordComplexityMetric analyzePassword(String password, Set<String> wordDict) {
        PasswordComplexityMetric metric = new PasswordComplexityMetric();
        int length = password.length();
        metric.setPasswordLength(length);
        int dictionaryWordCount = wordBreak(password, wordDict);
        metric.setDictionaryWordCount(dictionaryWordCount);
        calculateRepeatingCharactersCount(password, metric);
        calculateSequentialCharactersCount(password, metric);
        countCharacterTypes(password, metric);
        calculatePasswordComplexityScore(metric);

        return metric;
    }

    private static void calculatePasswordComplexityScore(PasswordComplexityMetric metric) {
        // Define weightage for each attribute (adjust values as needed)
        double lengthWeight = 0.3;
        double characterTypesWeight = 0.2;
        double dictionaryWordWeight = 0.1;
        double numericCharactersWeight = 0.1;
        double specialCharactersWeight = 0.1;
        double uppercaseLettersWeight = 0.1;
        double lowercaseLettersWeight = 0.1;
        double sequentialCharactersWeight = 0.05;
        double repeatingCharactersWeight = 0.05;

        // Normalize attribute values
        double normalizedLength = (double) metric.getPasswordLength() / MAX_PASSWORD_LENGTH;
        double normalizedCharacterTypes = (double) metric.getCharacterTypesUsed() / MAX_CHARACTER_TYPES;
        // Normalize other attributes similarly

        // Calculate weighted sum
        double passwordComplexityScore = lengthWeight * normalizedLength
                + characterTypesWeight * normalizedCharacterTypes
                + dictionaryWordWeight * metric.getDictionaryWordCount()
                + numericCharactersWeight * metric.getNumericCharactersCount()
                + specialCharactersWeight * metric.getSpecialCharactersCount()
                + uppercaseLettersWeight * metric.getUppercaseLettersCount()
                + lowercaseLettersWeight * metric.getLowercaseLettersCount()
                + sequentialCharactersWeight * metric.getSequentialCharactersCount()
                + repeatingCharactersWeight * metric.getRepeatingCharactersCount();

        metric.setPasswordComplexityScore(passwordComplexityScore);
    }

    private static void calculateSequentialCharactersCount(String password, PasswordComplexityMetric metric) {
        int count = 0;
        for (int i = 0; i < password.length() - 2; i++) {
            if (isSequential(password.charAt(i), password.charAt(i + 1), password.charAt(i + 2))) {
                count++;
            }
        }
        metric.setSequentialCharactersCount(count);
    }

    private static boolean isSequential(char a, char b, char c) {
        return (b == a + 1 && c == b + 1) || (b == a - 1 && c == b - 1);
    }

    private static void calculateRepeatingCharactersCount(String password, PasswordComplexityMetric metric) {
        int count = 0;
        for (int i = 0; i < password.length() - 1; i++) {
            if (password.charAt(i) == password.charAt(i + 1)) {
                count++;
            }
        }
        metric.setRepeatingCharactersCount(count);
    }

    private static void countCharacterTypes(String password, PasswordComplexityMetric metric) {
        int uppercase = 0;
        int lowercase = 0;
        int numbers = 0;
        int specialChar = 0;

        for (int i = 0; i < password.length(); i++) {
            int ascii = password.charAt(i);
            if (ascii >= 65 && ascii <= 90) uppercase++;
            if (ascii >= 97 && ascii <= 122) lowercase++;
            if (ascii >= 48 && ascii <= 57) numbers++;
            if ((ascii >= 33 && ascii <= 47) || (ascii >= 58 && ascii <= 64)) specialChar++;
        }

        metric.setNumericCharactersCount(numbers);
        metric.setSpecialCharactersCount(specialChar);
        metric.setUppercaseLettersCount(uppercase);
        metric.setLowercaseLettersCount(lowercase);
    }


    // https://leetcode.com/problems/word-break/solutions/4852733/bottom-up-dp-easy-to-understand/
    private static int wordBreak(String s, Set<String> wordDict) {
        int length = s.length();
        // Create a map to store whether a substring can be broken into words
        Map<String, Boolean> table = new HashMap<>();
        table.put("", true); // base case
        // Populate the map with the hash values of the words in the dictionary
        for (String wordHash : wordDict) {
            table.put(wordHash, true);
        }
        // Build the table from the least suffix to the original string
        for (int startIndex = length - 1; startIndex >= 0; startIndex--) {
            for (int endIndex = length - 1; endIndex >= startIndex; endIndex--) {
                String subString = s.substring(startIndex, endIndex + 1);
                String hashedSubString = hashCodeString(subString.getBytes(StandardCharsets.UTF_8));
                int subStrLength = endIndex - startIndex + 1;

                for (String wordHash : wordDict) {
                    // Check if the length of the substring matches the length of the word hash
                    if (subStrLength == wordHash.length()) {
                        // Check if the hash value of the substring matches any hash value in the dictionary
                        if (table.containsKey(subString) && table.containsKey(wordHash)) {
                            table.put(subString, true);
                            // We have found a substring that can successfully be broken into words, so break the loop
                            break;
                        }
                    }
                }
            }
        }
        // Count the occurrences of the original string in the table
        int count = 0;
        for (Map.Entry<String, Boolean> entry : table.entrySet()) {
            if (entry.getKey().equals(s) && entry.getValue()) {
                count++;
            }
        }
        return count;
    }

    private static String hashCodeString(byte[] data)  {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] encodedHash = digest.digest(data);

        StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
        for (byte hash : encodedHash) {
            String hex = Integer.toHexString(0xff & hash);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
