package dev.twiceb.apigateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dev.twiceb.apigateway.service.util.HmacCsrfToken;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/csrf-token")
public class CsrfTokenController {

    @GetMapping
    public ResponseEntity<Map<String, String>> getCsrfToken(HttpServletResponse response) {
        String tokenRaw = UUID.randomUUID().toString();
        String signedToken = HmacCsrfToken.generateSignedToken(tokenRaw);
        ResponseCookie cookie = ResponseCookie.from("csrftoken", signedToken).httpOnly(true)
                .secure(false).sameSite("Lax").path("/").build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(Map.of("csrftoken", signedToken));
    }

}
