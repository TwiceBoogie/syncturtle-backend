package dev.twiceb.userservice.controller.rest;

import dev.twiceb.userservice.dto.request.AuthenticationRequest;
import dev.twiceb.userservice.dto.response.AuthenticationResponse;
import dev.twiceb.userservice.mapper.AuthenticationMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static dev.twiceb.common.constants.PathConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(UI_V1_AUTH)
public class AuthenticationController {

    private final AuthenticationMapper authenticationMapper;

    @PostMapping(LOGIN)
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody AuthenticationRequest request,
            BindingResult bindingResult
    ) {
        return ResponseEntity.ok(authenticationMapper.login(request, bindingResult));
    }
}
