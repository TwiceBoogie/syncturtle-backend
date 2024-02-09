package dev.twiceb.common.security;

import dev.twiceb.common.exception.JwtAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
// import java.security.SecureRandom;
// import java.util.Base64;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static dev.twiceb.common.constants.ErrorMessage.JWT_TOKEN_EXPIRED;
import static dev.twiceb.common.constants.PathConstants.AUTH_USER_DEVICE_KEY;

@Component
public class JwtProvider {
    @Value("${jwt.header:Authorization}")
    private String authorizationHeader;

    @Value("${jwt.secretKey:tIlqaaFuXA7v6lxReXzO6+DxJ65azbXHdovliEcDYgk=}")
    private String secretKeyString;

    @Value("${jwt.deviceSecretKey:SDVlUnRVdlh5L0E/RChHK0tiUGVTaFZtWXEzdDZ3OXo=}")
    private String secretDeviceKeyString;

    private SecretKey secretKey;

    private SecretKey deviceSecretKey;

    @Value("${jwt.expiration:36000000}") // 10 hours in milliseconds
    private long validityInMilliseconds;

    // private String generatedSafeToken() {
    // SecureRandom random = new SecureRandom();
    // byte[] keyBytes = new byte[32];
    // random.nextBytes(keyBytes);
    // return Base64.getEncoder().encodeToString(keyBytes);
    // }

    @PostConstruct
    protected void init() {
        System.out.println(secretKeyString);
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyString));
        deviceSecretKey =  Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretDeviceKeyString));
    }

    // https://stackoverflow.com/questions/55102937/how-to-create-a-spring-security-key-for-signing-a-jwt-token
    public String createToken(String email, String role) {
        System.out.println(secretKey);
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + validityInMilliseconds * 1000);
        return Jwts.builder()
                .claim("ROLE", role)
                .issuer("${hostname:localhost}")
                .subject(email)
                .expiration(expiresAt)
                .signWith(secretKey)
                .compact();
    }

    public String createDeviceToken(String deviceKey) {
        LocalDateTime expirationDateTime = LocalDateTime.now().plusMonths(3);
        Date expirationDate = Date.from(expirationDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .claim("deviceKey", deviceKey)
                .issuer("${hostname:localhost}")
                .expiration(expirationDate)
                .signWith(deviceSecretKey)
                .compact();
    }

    public String resolveToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(authorizationHeader);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String resolveDeviceToken(ServerHttpRequest request) {
        return request.getHeaders().getFirst(AUTH_USER_DEVICE_KEY);
    }

    public boolean validateToken(String token, String type) {
        try {
            if (type.equals("deviceKey")) {
                Jws<Claims> claimsJws = Jwts.parser().verifyWith(deviceSecretKey).build().parseSignedClaims(token);
                return !claimsJws.getPayload().getExpiration().before(new Date());
            }
            Jws<Claims> claimsJws = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return !claimsJws.getPayload().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException exception) {
            throw new JwtAuthenticationException(JWT_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED);
        }
    }

    public String parseToken(String token) {
        Jws<Claims> body = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);

        return body.getPayload().getSubject();
    }
}
