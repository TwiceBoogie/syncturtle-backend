package dev.twiceb.userservice.controller.rest;

import dev.twiceb.common.dto.request.RequestMetadata;
import dev.twiceb.common.mapper.FieldErrorMapper;
import dev.twiceb.common.mapper.FieldErrorMapper.ValidationContext;
import dev.twiceb.common.spring.ServletHostResolverAdapterAutoConfiguration.ServletHostResolverAdapter;
import dev.twiceb.common.util.ExpandRoles;
import dev.twiceb.userservice.dto.request.AuthContextRequest;
import dev.twiceb.userservice.dto.request.MagicCodeRequest;
import dev.twiceb.userservice.dto.request.RegistrationRequest;
import dev.twiceb.userservice.dto.response.AuthenticationResponse;
import dev.twiceb.userservice.mapper.RegistrationMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static dev.twiceb.common.constants.PathConstants.*;
import java.net.URI;
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
        AuthenticationResponse res = registrationMapper.magicRegistration(payload);
        String roles = String.join(",", ExpandRoles.expand(res.getRole()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("X-Internal-UserId", res.getUserId().toString())
                .header("X-Internal-Roles", roles).build();
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
            return ResponseEntity.status(HttpStatus.FOUND).location(location).build();
        }

        AuthContextRequest<RegistrationRequest> payload =
                new AuthContextRequest<>(metadata, request);

        AuthenticationResponse res = registrationMapper.signUp(payload);

        String base =
                resolverAdapter.resolve(httpRequest, false, false, true, res.getRedirectionPath());
        URI location = resolverAdapter.buildUrl(base, Map.of("next_path", nextPath));
        String roles = String.join(",", ExpandRoles.expand(res.getRole()));

        return ResponseEntity.ok().header("X-Internal-UserId", res.getUserId().toString())
                .header("X-Internal-Roles", roles).location(location).build();
    }

}
