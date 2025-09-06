package dev.twiceb.userservice.controller.rest;

import dev.twiceb.common.dto.request.RequestMetadata;
import dev.twiceb.common.dto.response.TokenGrant;
import dev.twiceb.userservice.dto.request.*;
import dev.twiceb.userservice.dto.response.AccessTokenResponse;
import dev.twiceb.userservice.dto.response.AuthenticationResponse;
import dev.twiceb.userservice.dto.response.MagicCodeResponse;
import dev.twiceb.userservice.dto.response.MagicKeyResponse;
import dev.twiceb.userservice.mapper.AuthenticationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static dev.twiceb.common.constants.PathConstants.*;
import java.time.Duration;
import java.time.Instant;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping(UI_V1_AUTH)
public class AuthenticationController {

    private final AuthenticationMapper authenticationMapper;

    @PostMapping(CHECK_EMAIL)
    public ResponseEntity<MagicCodeResponse> checkEmail(@RequestBody MagicCodeRequest request,
            BindingResult bindingResult) {
        return ResponseEntity.ok(authenticationMapper.checkEmail(request));
    }

    @PostMapping(GENERATE_MAGIC_CODE)
    public ResponseEntity<MagicKeyResponse> generateMagicCode(@RequestBody MagicCodeRequest request,
            BindingResult bindingResult) {
        return ResponseEntity.ok(authenticationMapper.generateMagicCodeAuth(request));
    }

    @PostMapping(GENERATE_MAGIC_CODE_DEVICE)
    public ResponseEntity<MagicKeyResponse> generateMagicCodeDevice(
            @RequestBody MagicCodeRequest request, BindingResult bindingResult) {
        return ResponseEntity.ok(authenticationMapper.generateMagicCodeDevice(request));
    }


    @PostMapping(MAGIC_LOGIN)
    public ResponseEntity<AuthenticationResponse> magicLogin(@RequestBody MagicCodeRequest request,
            BindingResult bindingResult,
            @RequestAttribute("requestMetadata") RequestMetadata metadata) {
        AuthContextRequest<MagicCodeRequest> payload = new AuthContextRequest<>(metadata, request);
        return ResponseEntity.ok(authenticationMapper.magicLogin(payload));
    }

    @PostMapping(LOGIN)
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request,
            BindingResult bindingResult,
            @RequestAttribute("requestMetadata") RequestMetadata metadata) {
        AuthContextRequest<AuthenticationRequest> payload =
                new AuthContextRequest<>(metadata, request);

        return ResponseEntity.ok(authenticationMapper.login(payload));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refreshToken(
            @CookieValue(name = "token") String refreshToken,
            @RequestAttribute("requestMetadata") RequestMetadata metadata) {
        AuthContextRequest<RefreshTokenRequest> authContextRequest =
                new AuthContextRequest<>(metadata, new RefreshTokenRequest(refreshToken));
        TokenGrant res = authenticationMapper.refreshToken(authContextRequest);

        ResponseCookie cookie =
                ResponseCookie.from("token", res.getRc().getToken()).httpOnly(true).secure(false)
                        .domain(".127.0.0.1.nip.io").sameSite("Lax").path(UI_V1_AUTH + "/refresh")
                        .maxAge(Duration.between(Instant.now(), res.getRc().getExp())).build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().headers(headers)
                .body(new AccessTokenResponse(res.getAt().getJwt(), res.getAt().getExp()));
    }
}
