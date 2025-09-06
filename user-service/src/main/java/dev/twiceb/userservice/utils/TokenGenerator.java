package dev.twiceb.userservice.utils;

import java.security.SecureRandom;
import java.util.Base64;

// final prevents subclassing this class
public final class TokenGenerator {
    private static final SecureRandom RNG = new SecureRandom();

    // canocial java utility-class idiom
    private TokenGenerator() {
        throw new AssertionError();
    }

    public static TokenPair newPair() {
        return new TokenPair(randomUrlToken128(), randomUrlToken256());
    }

    public static String newToken(int numBytes) {
        byte[] bytes = new byte[numBytes];
        // SecureRandom.getInstanceStrong().nextBytes(bytes);
        RNG.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String randomUrlToken128() {
        byte[] bytes = new byte[16];
        RNG.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // 256bit URL safe token
    public static String randomUrlToken256() {
        byte[] bytes = new byte[32];
        RNG.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public record TokenPair(String handle, String secret) {
    };
}
