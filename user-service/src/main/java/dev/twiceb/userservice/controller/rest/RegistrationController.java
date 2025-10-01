package dev.twiceb.userservice.controller.rest;

import dev.twiceb.common.dto.request.RequestMetadata;
import dev.twiceb.common.exception.AuthException;
import dev.twiceb.common.mapper.FieldErrorMapper;
import dev.twiceb.common.mapper.FieldErrorMapper.ValidationContext;
import dev.twiceb.common.spring.ServletHostResolverAdapterAutoConfiguration.ServletHostResolverAdapter;
import dev.twiceb.userservice.dto.request.AuthContextRequest;
import dev.twiceb.userservice.dto.request.MagicCodeRequest;
import dev.twiceb.userservice.dto.request.RegistrationRequest;
import dev.twiceb.userservice.dto.response.AuthenticationResponse;
import dev.twiceb.userservice.mapper.RegistrationMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static dev.twiceb.common.constants.PathConstants.*;
import java.net.URI;
import java.time.Duration;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping(AUTH)
public class RegistrationController {

    private final ServletHostResolverAdapter resolverAdapter;
    private final RegistrationMapper registrationMapper;

    @PostMapping(MAGIC_SIGN_UP)
    public ResponseEntity<AuthenticationResponse> magicSignup(@RequestBody MagicCodeRequest request,
            BindingResult bindingResult,
            @RequestAttribute("requestMetadata") RequestMetadata meta) {
        AuthContextRequest<MagicCodeRequest> payload = new AuthContextRequest<>(meta, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registrationMapper.magicRegistration(payload));
    }

    // FORM: application/x-www-form-urlencoded (Heroui <Form/> or plain <form/>)
    @PostMapping(value = "/sign-up", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> signUp(@Valid @ModelAttribute RegistrationRequest request,
            BindingResult bindingResult,
            @RequestAttribute("requestMetadata") RequestMetadata metadata,
            HttpServletRequest httpRequest) {
        String nextPath = request.getNextPath();
        nextPath = resolverAdapter.validateNextPath(nextPath);

        if (bindingResult.hasErrors()) {
            AuthException exc =
                    FieldErrorMapper.mapToAuthException(bindingResult, ValidationContext.SIGN_UP);
            Map<String, String> params = exc.getMapVersion();
            if (!nextPath.isBlank()) {
                params.put("next_path", nextPath);
            }
            String base = resolverAdapter.resolve(httpRequest, false, false, true, null);
            URI location = resolverAdapter.buildUrl(base, params);

            return ResponseEntity.status(302).location(location).build();
        }

        AuthContextRequest<RegistrationRequest> payload =
                new AuthContextRequest<>(metadata, request);
        AuthenticationResponse res = registrationMapper.signUp(payload);

        ResponseCookie refreshToken = ResponseCookie
                .from("token", res.getTokenGrant().getRt().getToken()).httpOnly(true).secure(false)
                .path("/").maxAge(Duration.ofDays(30)).sameSite("Strict").build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshToken.toString());

        String base =
                resolverAdapter.resolve(httpRequest, false, false, true, res.getRedirectionPath());
        URI location = resolverAdapter.buildUrl(base, Map.of("next_path", nextPath));

        return ResponseEntity.status(302).location(location).headers(headers).build();
    }


    // FORM: application/json (SPA/fetch/mobile)
    @PostMapping(value = "/sign-up", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthenticationResponse> signUpJson(
            @RequestBody @Valid RegistrationRequest request, BindingResult bindingResult,
            @RequestAttribute("requestMetadata") RequestMetadata metadata,
            HttpServletRequest httpRequest) {
        String nextPath = request.getNextPath();
        if (bindingResult.hasErrors()) {
            Map<String, String> params = FieldErrorMapper
                    .mapToAuthException(bindingResult, ValidationContext.SIGN_UP).getMapVersion();
            nextPath = resolverAdapter.validateNextPath(nextPath);
            if (!nextPath.isBlank()) {
                params.put("next_path", nextPath);
            }
            String base = resolverAdapter.resolve(httpRequest, false, false, false, null);
            URI location = resolverAdapter.buildUrl(base, params);
            return ResponseEntity.status(302).location(location).build();
        }
        AuthContextRequest<RegistrationRequest> payload =
                new AuthContextRequest<>(metadata, request);

        AuthenticationResponse res = registrationMapper.signUp(payload);

        String base =
                resolverAdapter.resolve(httpRequest, false, false, true, res.getRedirectionPath());
        URI location = resolverAdapter.buildUrl(base, Map.of("next_path", nextPath));
        return ResponseEntity.ok().location(location).body(res);
    }

}
