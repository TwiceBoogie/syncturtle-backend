package dev.twiceb.userservice.service.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;

@Component
public final class HmacHasher implements Hasher {
    private static final String SECRET = "change-me-32bytes-min";

    @Override
    public String hash(String raw) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean matches(String raw, String hashed) {
        if (raw == null || hashed == null)
            return false;
        if (raw.length() != hashed.length())
            return false;
        int r = 0;
        for (int i = 0; i < raw.length(); i++) {
            r |= raw.charAt(i) ^ hashed.charAt(i);
        }
        return r == 0;
    }
}
