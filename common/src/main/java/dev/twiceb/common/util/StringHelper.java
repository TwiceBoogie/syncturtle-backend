package dev.twiceb.common.util;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

public final class StringHelper {
    private StringHelper() {}

    public static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    public static String nvl(String val, String def) {
        if (isBlank(val)) {
            return def.trim();
        }
        return val.trim();
    }

    public static String firstNonBlank(String... values) {
        if (values == null)
            return null;
        for (String v : values) {
            if (!isBlank(v))
                return v;
        }
        return null;
    }

    @SafeVarargs
    public static <T> T firstNonNull(T... values) {
        if (values == null)
            return null;
        for (T v : values) {
            if (Objects.nonNull(v))
                return v;
        }
        return null;
    }

    public static String normalize(String s) {
        return s == null ? null : s.trim().toLowerCase();
    }

    public static String normalizeAndTrim(String s) {
        if (s == null)
            return null;
        String nfkc = Normalizer.normalize(s, Form.NFKC);
        return nfkc.trim().toLowerCase();
    }

    public static String generateRandomUsername() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String normalizeEmail(String email) {
        return normalize(email);
    }

    // ---- Validation (lightweight)
    private static final Pattern EMAILISH =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,63}$", Pattern.CASE_INSENSITIVE);

    public static boolean isEmailish(String s) {
        return !isBlank(s) && EMAILISH.matcher(s.trim()).matches();
    }

    public static boolean isUUID(String s) {
        if (isBlank(s))
            return false;
        try {
            UUID.fromString(s.trim());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Optional<UUID> tryParseUUID(String s) {
        if (isBlank(s))
            return Optional.empty();
        try {
            return Optional.of(UUID.fromString(s.trim()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static boolean isNumeric(String s) {
        return !isBlank(s) && s.chars().allMatch(Character::isDigit);
    }

    public static boolean hasLengthAtMost(String s, int max) {
        return s == null || s.length() <= max;
    }

    // ---- Transform / formatting
    private static final Pattern WS = Pattern.compile("\\s+");

    public static String collapseWhitespace(String s) {
        return isBlank(s) ? s : WS.matcher(s.trim()).replaceAll(" ");
    }

    public static String truncate(String s, int max) {
        if (s == null || s.length() <= max)
            return s;
        return (max <= 1) ? s.substring(0, max) : s.substring(0, max - 1) + "…";
    }

    public static String stripDiacritics(String s) {
        if (s == null)
            return null;
        String norm = Normalizer.normalize(s, Form.NFD);
        return norm.replaceAll("\\p{M}+", "");
    }

    public static String slugify(String s) {
        if (s == null)
            return null;
        String base = stripDiacritics(s).toLowerCase();
        base = base.replaceAll("[^a-z0-9]+", "-");
        base = base.replaceAll("^-+|-+$", "");
        return base;
    }

    public static String toSnakeCase(String s) {
        if (isBlank(s))
            return s;
        String x = stripDiacritics(s).trim();
        x = x.replaceAll("([a-z0-9])([A-Z])", "$1_$2");
        x = x.replaceAll("[^A-Za-z0-9]+", "_");
        return x.toLowerCase().replaceAll("^_+|_+$", "");
    }

    public static String toKebabCase(String s) {
        return toSnakeCase(s).replace('_', '-');
    }

    public static String capitalize(String s) {
        if (isBlank(s))
            return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static String titleCase(String s) {
        if (isBlank(s))
            return s;
        String[] parts = collapseWhitespace(s).split(" ");
        for (int i = 0; i < parts.length; i++)
            parts[i] = capitalize(parts[i].toLowerCase());
        return String.join(" ", parts);
    }

    // ---- Safe comparisons
    public static boolean equalsIgnoreCaseSafe(String a, String b) {
        return Objects.equals(normalize(a), normalize(b));
    }

    /** Constant-time compare for secrets (not for long strings in hot paths) */
    public static boolean equalsConstantTime(String a, String b) {
        if (a == null || b == null)
            return a == b;
        byte[] x = a.getBytes(), y = b.getBytes();
        int len = Math.max(x.length, y.length), diff = x.length ^ y.length;
        for (int i = 0; i < len; i++)
            diff |= (x[i % x.length] ^ y[i % y.length]);
        return diff == 0;
    }

    // ---- Email/phone helpers
    public static String deriveDisplayNameFromEmail(String email) {
        if (isBlank(email))
            return randomAlpha(6);
        String e = email.trim();
        int at = e.indexOf('@');
        return at > 0 ? e.substring(0, at) : randomAlpha(6);
    }

    public static String maskEmailLocalPart(String email) {
        if (!isEmailish(email))
            return email;
        String[] parts = email.split("@", 2);
        String local = parts[0];
        if (local.length() <= 2)
            return "*@" + parts[1];
        return local.charAt(0) + "***" + local.charAt(local.length() - 1) + "@" + parts[1];
    }

    public static String maskPhone(String phone) {
        if (isBlank(phone))
            return phone;
        String digits = phone.replaceAll("\\D", "");
        if (digits.length() < 4)
            return "****";
        return "****" + digits.substring(digits.length() - 4);
    }

    // ---- Random (non-crypto)
    private static final String ALPHA = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALNUM = ALPHA + "0123456789";
    private static final Random RND = new Random();

    public static String randomAlpha(int n) {
        return randomFrom(ALPHA, n);
    }

    public static String randomAlphaNumeric(int n) {
        return randomFrom(ALNUM, n);
    }

    public static String randomHex(int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++)
            sb.append(Integer.toHexString(RND.nextInt(16)));
        return sb.toString();
    }

    private static String randomFrom(String alphabet, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++)
            sb.append(alphabet.charAt(RND.nextInt(alphabet.length())));
        return sb.toString();
    }

    // ---- Collections
    public static String joinNonBlank(Collection<String> parts, String delimiter) {
        return parts == null ? null
                : parts.stream().filter(p -> !isBlank(p)).reduce((a, b) -> a + delimiter + b)
                        .orElse("");
    }

    public static List<String> splitAndTrim(String s, String delimiterRegex) {
        if (isBlank(s))
            return List.of();
        String[] arr = s.split(delimiterRegex);
        List<String> out = new ArrayList<>(arr.length);
        for (String a : arr) {
            String t = a.trim();
            if (!t.isEmpty())
                out.add(t);
        }
        return out;
    }
}
