package dev.twiceb.common.security;

import dev.twiceb.common.exception.JwtAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

import static dev.twiceb.common.constants.ErrorMessage.JWT_TOKEN_EXPIRED;

@Component
public class JwtProvider {
    @Value("${jwt.header:Authorization}")
    private String authorizationHeader;

    @Value("${jwt.secret:randomSecretKey}")
    private String secretKeyString;

    private SecretKey secretKey;

    @Value("${jwt.expiration:36000000}") // 10 hours in milliseconds
    private long validityInMilliseconds;

    @PostConstruct
    protected void init() {
        byte[] decodedKey = Base64.getDecoder().decode(secretKeyString);
        secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    public String createToken(String email, String role) {
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

    public String resolveToken(ServerHttpRequest request) {
        return request.getHeaders().getFirst(authorizationHeader);
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return !claimsJws.getPayload().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException exception) {
            throw new JwtAuthenticationException(JWT_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED);
        }
    }

    public String parseToken(String token) {
        Jws<Claims> body = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
        System.out.println(body);

        return body.getPayload().getSubject();
    }
}
