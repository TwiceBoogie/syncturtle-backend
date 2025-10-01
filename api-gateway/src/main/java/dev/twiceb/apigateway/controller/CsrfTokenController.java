package dev.twiceb.apigateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import dev.twiceb.apigateway.service.util.HmacCsrfToken;
import reactor.core.publisher.Mono;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
// import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/csrf-token")
public class CsrfTokenController {

    // @CrossOrigin(origins = {"https://127.0.0.1.nip.io"}, allowCredentials = "true",
    // allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.OPTIONS})
    @GetMapping
    public Mono<ResponseEntity<Map<String, String>>> getCsrfToken(ServerHttpResponse response) {
        String raw = UUID.randomUUID().toString();
        String signed = HmacCsrfToken.generateSignedToken(raw);
        // __Host-csrf strict and must have secure(true), no domain, path
        ResponseCookie cookie = ResponseCookie.from("csrftoken", signed).httpOnly(true)
                .secure(false).sameSite("Strict").path("/").build();
        response.addCookie(cookie);
        return Mono.just(ResponseEntity.ok(Map.of("csrfToken", signed)));
    }

}
