package dev.twiceb.instanceservice.controller.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dev.twiceb.common.constants.PathConstants;
import dev.twiceb.common.dto.internal.AuthAdminResult;
import dev.twiceb.common.dto.request.AdminSignupRequest;
import dev.twiceb.common.dto.request.RequestMetadata;
import dev.twiceb.common.util.ExpandRoles;
import dev.twiceb.instanceservice.controller.util.CacheControl;
import dev.twiceb.instanceservice.controller.util.CacheResponse;
import dev.twiceb.instanceservice.controller.util.InvalidateCacheRedis;
import dev.twiceb.instanceservice.dto.request.InstanceConfigurationUpdateRequest;
import dev.twiceb.instanceservice.dto.response.InstanceAdminResponse;
import dev.twiceb.instanceservice.dto.response.InstanceSetupResponse;
import dev.twiceb.instanceservice.mapper.InstanceMapper;
import dev.twiceb.instanceservice.service.impl.InstanceAdminPermissionImpl;
import dev.twiceb.instanceservice.util.PermissionClasses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;

@RestController
@RequestMapping("/instances")
@RequiredArgsConstructor
@PermissionClasses({InstanceAdminPermissionImpl.class})
public class InstanceController {

    private final InstanceMapper instanceMapper;

    @GetMapping()
    @CacheResponse(cacheName = "instance", ttl = 60 * 60 * 2, user = false)
    @CacheControl(privateCache = true, maxAge = 12)
    public ResponseEntity<InstanceSetupResponse> getInstanceInfo() {
        return ResponseEntity.ok(instanceMapper.getInstanceInfo());
    }

    // patch method on itself?

    @PatchMapping(PathConstants.CONFIGURATION)
    @InvalidateCacheRedis(cacheName = "instance", path = PathConstants.UI_V1_INSTANCE,
            multiple = true)
    public ResponseEntity<Void> updateConfigurations(
            @RequestBody @Valid InstanceConfigurationUpdateRequest request) {
        return ResponseEntity.noContent().build();
    }

    // admins
    // create instance admin POST

    // delete instance admin DELETE

    @GetMapping("/admins")
    @CacheResponse(cacheName = "instance", ttl = 60 * 60 * 2, user = false)
    public ResponseEntity<List<InstanceAdminResponse>> getInstanceAdmins() {
        return ResponseEntity.ok().body(instanceMapper.getInstanceAdmins());
    }

    @PostMapping("/admins/sign-up")
    @InvalidateCacheRedis(cacheName = "instance", path = "instances", multiple = true, user = false)
    public ResponseEntity<Void> adminSignup(@Valid @RequestBody AdminSignupRequest request,
            BindingResult bindingResult, @RequestAttribute("requestMetadata") RequestMetadata meta,
            HttpServletRequest httpRequest) {
        if (bindingResult.hasErrors()) {
            System.out.println("error has occured");
            System.out.println(request);
        }

        AuthAdminResult res = instanceMapper.adminSignup(request);
        String roles = String.join(",", ExpandRoles.expand(res.getRole()));
        return ResponseEntity.ok().header("X-Internal-UserId", res.getUserId().toString())
                .header("X-Internal-Roles", roles).build();
    }

}
