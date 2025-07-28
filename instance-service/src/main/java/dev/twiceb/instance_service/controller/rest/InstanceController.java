package dev.twiceb.instance_service.controller.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dev.twiceb.instance_service.dto.response.InstanceSetupResponse;
import dev.twiceb.instance_service.mapper.InstanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/ui/v1/instance")
@RequiredArgsConstructor
public class InstanceController {

    private final InstanceMapper instanceMapper;

    @GetMapping("/")
    public ResponseEntity<InstanceSetupResponse> getInstanceInfo() {
        return ResponseEntity.ok(instanceMapper.getInstanceInfo());
    }

}
