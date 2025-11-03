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
import dev.twiceb.instanceservice.dto.request.InstanceInfoUpdateRequest;
import dev.twiceb.instanceservice.dto.response.InstanceAdminResponse;
import dev.twiceb.instanceservice.dto.response.InstanceInfoResponse;
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

    @PatchMapping()
    @InvalidateCacheRedis(cacheName = "instance", path = "instances", multiple = true, user = false)
    public ResponseEntity<InstanceInfoResponse> updateInstanceInfo(
            @Valid @RequestBody InstanceInfoUpdateRequest request) {
        return ResponseEntity.ok(instanceMapper.updateInstanceInfo(request));
    }

    // patch method on itself?

    @PatchMapping(PathConstants.CONFIGURATION)
    @InvalidateCacheRedis(cacheName = "instance", path = "instances", multiple = true, user = false)
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
        // check if payload is valid
        if (bindingResult.hasErrors()) {
            System.out.println("error has occured");
            System.out.println(request);
        }

        AuthAdminResult res = instanceMapper.adminSignup(request);
        // set roles as a string so it works in the response header
        String roles = String.join(",", ExpandRoles.expand(res.getRole()));
        // we return 200 and internal headers so api-gateway sets logged in user in redis
        return ResponseEntity.ok().header("X-Internal-UserId", res.getUserId().toString())
                .header("X-Internal-Roles", roles).build();
    }

}
