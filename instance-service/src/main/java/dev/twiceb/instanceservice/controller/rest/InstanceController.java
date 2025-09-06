package dev.twiceb.instanceservice.controller.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dev.twiceb.common.constants.PathConstants;
import dev.twiceb.common.dto.request.AdminSignupRequest;
import dev.twiceb.common.dto.request.RequestMetadata;
import dev.twiceb.common.dto.response.TokenGrant;
import dev.twiceb.common.util.BaseHostResolver;
import dev.twiceb.instanceservice.controller.util.CacheControl;
import dev.twiceb.instanceservice.controller.util.CacheResponse;
import dev.twiceb.instanceservice.controller.util.InvalidateCacheRedis;
import dev.twiceb.instanceservice.dto.request.InstanceConfigurationUpdateRequest;
import dev.twiceb.instanceservice.dto.response.InstanceSetupResponse;
import dev.twiceb.instanceservice.mapper.InstanceMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;

import static dev.twiceb.common.constants.PathConstants.*;

@RestController
@RequestMapping(PathConstants.UI_V1_INSTANCE)
@RequiredArgsConstructor
public class InstanceController {

    private final InstanceMapper instanceMapper;
    private final BaseHostResolver hostResolver;

    @GetMapping()
    @CacheResponse(cacheName = "instance", ttl = 60 * 60 * 2, user = false)
    @CacheControl(privateCache = true, maxAge = 12)
    public ResponseEntity<InstanceSetupResponse> getInstanceInfo() {
        return ResponseEntity.ok(instanceMapper.getInstanceInfo());
    }

    @PatchMapping(PathConstants.CONFIGURATION)
    @InvalidateCacheRedis(cacheName = "instance", path = PathConstants.UI_V1_INSTANCE,
            multiple = true)
    public ResponseEntity<Void> updateConfigurations(
            @RequestBody @Valid InstanceConfigurationUpdateRequest request) {
        return ResponseEntity.noContent().build();
    }

    // FORM: application/x-www-form-urlencoded (Heroui <Form/> or plain <form/>)
    @PostMapping(value = "/sign-up", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @InvalidateCacheRedis(cacheName = "instance", path = PathConstants.UI_V1_INSTANCE)
    public ResponseEntity<Void> adminSignup(@ModelAttribute @Valid AdminSignupRequest request,
            BindingResult bindingResult,
            @RequestAttribute("requestMetadata") RequestMetadata meta) {
        if (bindingResult.hasErrors()) {
            // do something with the errors;
        }

        TokenGrant tokenGrant = instanceMapper.adminSignup(request);

        ResponseCookie cookie = ResponseCookie.from("token", tokenGrant.getRc().getToken())
                .httpOnly(true).secure(false).sameSite("Lax").path(UI_V1_AUTH + "/refresh")
                .maxAge(Duration.between(Instant.now(), tokenGrant.getRc().getExp())).build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        String base = hostResolver.resolve(true, false, false);
        URI location = hostResolver.buildUrl(base + "/general", null);

        return ResponseEntity.status(302).location(location).headers(headers).build();
    }

}
