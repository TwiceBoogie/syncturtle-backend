package dev.twiceb.userservice.utils;

import java.security.SecureRandom;

public final class MagicCodeGenerator {
    private static final SecureRandom RAND = new SecureRandom();
    private static final char[] ALPHA = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    public static String humanReadableToken() {
        return group(4) + "-" + group(4) + "-" + group(4);
    }

    private static String group(int n) {
        char[] buf = new char[n];
        for (int i = 0; i < n; i++) {
            buf[i] = ALPHA[RAND.nextInt(ALPHA.length)];
        }
        return new String(buf);
    }
}
