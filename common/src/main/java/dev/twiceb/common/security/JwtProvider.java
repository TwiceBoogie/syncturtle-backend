package dev.twiceb.common.security;

import dev.twiceb.common.exception.JwtAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
// import java.security.SecureRandom;
// import java.util.Base64;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import static dev.twiceb.common.constants.ErrorMessage.JWT_TOKEN_EXPIRED;
// import static dev.twiceb.common.constants.PathConstants.AUTH_USER_DEVICE_KEY;

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

    @Value("${jwt.expiration:14400000}") // 4hours in milliseconds
    private long validityInMilliseconds;

    // private final PrivateKey priv;
    private final String kid = "gw-2025-08-01";

    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyString));
        deviceSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretDeviceKeyString));
    }

    public AccessToken createAccessToken(UUID userId, String role) {
        Instant now = Instant.now();
        Instant exp = now.plus(10, ChronoUnit.MINUTES);
        String jwt = Jwts.builder().issuer("localhost").subject(userId.toString())
                .claim("ROLE", role).issuedAt(Date.from(now)).expiration(Date.from(exp))
                .signWith(secretKey, Jwts.SIG.HS256).compact();
        return new AccessToken(jwt, exp);
    }

    // internal jwt asymmetric
    // public String mint(String userId, String tenantId, Collection<String> roles, String audience)
    // {
    // Instant now = Instant.now();
    // Instant exp = now.plusSeconds(90);
    // return Jwts.builder().header().keyId(kid).and().issuer("gw") // gateway issuer
    // .audience().add(audience).and() // e.g. "internal:user-svc"
    // .subject(userId).claim("ten", tenantId).claim("roles", roles)
    // .id(UUID.randomUUID().toString()) // jti
    // .issuedAt(Date.from(now)).expiration(Date.from(exp)).signWith(priv, Jwts.SIG.RS256) //
    // asymmetric
    // .compact();
    // }

    // https://stackoverflow.com/questions/55102937/how-to-create-a-spring-security-key-for-signing-a-jwt-token
    public String createToken(UUID userId, String role) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + validityInMilliseconds);
        return Jwts.builder().claim("ROLE", role).issuer("localhost").subject(userId.toString())
                .expiration(expiresAt).signWith(secretKey).compact();
    }

    public String createDeviceToken(String deviceKey) {
        LocalDateTime expirationDateTime = LocalDateTime.now().plusMonths(3);
        Date expirationDate =
                Date.from(expirationDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder().claim("deviceKey", deviceKey).issuer("localhost")
                .expiration(expirationDate).signWith(deviceSecretKey).compact();
    }

    public String resolveToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(authorizationHeader);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String resolveDeviceToken(ServerHttpRequest request) {
        List<HttpCookie> cookies = request.getCookies().get("deviceToken");
        if (cookies != null && !cookies.isEmpty()) {
            return cookies.get(0).getValue();
        }
        return null;
    }

    public boolean validateToken(String token, String type) {
        try {
            if (type.equals("deviceKey")) {
                Jws<Claims> claimsJws =
                        Jwts.parser().verifyWith(deviceSecretKey).build().parseSignedClaims(token);
                return !claimsJws.getPayload().getExpiration().before(new Date());
            }
            Jws<Claims> claimsJws =
                    Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return !claimsJws.getPayload().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException exception) {
            System.out.println("Error has occured");
            throw new JwtAuthenticationException(JWT_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED);
        }
    }

    public String parseToken(String token) {
        Jws<Claims> body = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);

        return body.getPayload().getSubject();
    }

    public String parseDeviceToken(String token) {
        Jws<Claims> body =
                Jwts.parser().verifyWith(deviceSecretKey).build().parseSignedClaims(token);
        return (String) body.getPayload().get("deviceKey");
    }

    @Getter
    @AllArgsConstructor
    public static class AccessToken {
        private final String jwt;
        private final Instant exp;
    }
}
