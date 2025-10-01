package dev.twiceb.apigateway.service.util;

import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacCsrfToken {

    private static final String HMAC_ALGO = "HmacSHA256";
    private static final String SECRET = "random-stuff";

    public static String generateSignedToken(String token) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(SECRET.getBytes(), HMAC_ALGO));
            byte[] hmac = mac.doFinal(token.getBytes());
            return token + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(hmac);
        } catch (Exception e) {
            throw new RuntimeException("Failled to sign token", e);
        }
    }

    public static boolean isValid(String tokenWithSignature) {
        try {
            String[] parts = tokenWithSignature.split("\\.");
            if (parts.length != 2)
                return false;
            String token = parts[0];
            String expected = generateSignedToken(token);
            return expected.equals(tokenWithSignature);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        if (a.length() != b.length()) {
            return false;
        }
        int r = 0;
        for (int i = 0; i < a.length(); i++) {
            r |= a.charAt(i) ^ b.charAt(i);
        }
        return r == 0;
    }

}
