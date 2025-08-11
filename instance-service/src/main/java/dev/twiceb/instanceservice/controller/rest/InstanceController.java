package dev.twiceb.instanceservice.controller.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dev.twiceb.instanceservice.controller.util.CacheControl;
import dev.twiceb.instanceservice.controller.util.CacheResponse;
import dev.twiceb.instanceservice.dto.response.InstanceSetupResponse;
import dev.twiceb.instanceservice.mapper.InstanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/ui/v1/instance")
@RequiredArgsConstructor
public class InstanceController {

    private final InstanceMapper instanceMapper;

    @GetMapping("/")
    @CacheResponse(cacheName = "instance", ttl = 3600, user = false)
    @CacheControl(privateCache = true, maxAge = 12)
    public ResponseEntity<InstanceSetupResponse> getInstanceInfo() {
        return ResponseEntity.ok(instanceMapper.getInstanceInfo());
    }

}
