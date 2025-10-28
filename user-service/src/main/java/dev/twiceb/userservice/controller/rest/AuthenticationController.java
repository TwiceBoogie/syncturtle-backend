package dev.twiceb.userservice.controller.rest;

import dev.twiceb.common.dto.request.RequestMetadata;
import dev.twiceb.common.util.ExpandRoles;
import dev.twiceb.userservice.dto.request.*;
import dev.twiceb.userservice.dto.response.AuthenticationResponse;
import dev.twiceb.userservice.dto.response.MagicCodeResponse;
import dev.twiceb.userservice.dto.response.MagicKeyResponse;
import dev.twiceb.userservice.mapper.AuthenticationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static dev.twiceb.common.constants.PathConstants.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(AUTH)
public class AuthenticationController {

    private final AuthenticationMapper authenticationMapper;

    @PostMapping(CHECK_EMAIL)
    public ResponseEntity<MagicCodeResponse> checkEmail(@RequestBody MagicCodeRequest request,
            BindingResult bindingResult) {
        log.info("endpoint hit");
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

        AuthenticationResponse res = authenticationMapper.magicLogin(payload);

        String roles = String.join(",", ExpandRoles.expand(res.getRole()));
        return ResponseEntity.ok().header("X-Internal-UserId", res.getUserId().toString())
                .header("X-Internal-Roles", roles).build();
    }

    @PostMapping(LOGIN)
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request,
            BindingResult bindingResult,
            @RequestAttribute("requestMetadata") RequestMetadata metadata) {
        AuthContextRequest<AuthenticationRequest> payload =
                new AuthContextRequest<>(metadata, request);

        AuthenticationResponse res = authenticationMapper.login(payload);

        String roles = String.join(",", ExpandRoles.expand(res.getRole()));
        return ResponseEntity.ok().header("X-Internal-UserId", res.getUserId().toString())
                .header("X-Internal-Roles", roles).build();
    }
}
