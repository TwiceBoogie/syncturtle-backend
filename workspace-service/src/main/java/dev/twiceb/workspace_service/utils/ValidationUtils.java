package dev.twiceb.workspace_service.utils;

import java.util.Set;
import java.util.regex.Pattern;

// TODO: retype
public final class ValidationUtils {
    private ValidationUtils() {}

    // keep this aligned with your Django list
    private static final Set<String> RESTRICTED_SLUGS =
            Set.of("api", "admin", "god-mode", "login", "signup", "settings", "auth", "static");

    private static final Pattern SLUG_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");

    // Safe URL detector (like your Python version)
    private static final Pattern URL_PATTERN = Pattern.compile(
            "(?i)(https?://\\S+|www\\.[^\\s]+|\\b[a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9]\\.[^\\s]{2,})");

    public static boolean containsUrl(String s) {
        if (s == null)
            return false;
        if (s.length() > 1000)
            return false; // defensive
        for (String line : s.split("\n")) {
            if (line.length() > 500)
                line = line.substring(0, 500);
            if (URL_PATTERN.matcher(line).find())
                return true;
        }
        return false;
    }

    public static void validateSlug(String slug) {
        if (slug == null || slug.isBlank())
            throw new IllegalArgumentException("Slug is required");
        if (RESTRICTED_SLUGS.contains(slug))
            throw new IllegalArgumentException("Slug is not valid");
        if (!SLUG_PATTERN.matcher(slug).matches())
            throw new IllegalArgumentException(
                    "Slug can only contain letters, numbers, hyphens (-), and underscores (_)");
    }

    public static void validateName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name is required");
        if (containsUrl(name))
            throw new IllegalArgumentException("Name must not contain URLs");
        if (name.length() > 80)
            throw new IllegalArgumentException("The maximum length for name is 80");
    }
}
