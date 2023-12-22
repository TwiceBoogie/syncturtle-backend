package dev.twiceb.passwordservice.controller.rest;

import dev.twiceb.common.dto.response.GenericResponse;
import dev.twiceb.passwordservice.dto.request.CreatePasswordRequest;
import dev.twiceb.passwordservice.mapper.PasswordMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static dev.twiceb.common.constants.PathConstants.*;

@RestController
@RequestMapping(UI_V1_DASHBOARD)
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordMapper passwordMapper;
    // testing
    // @GetMapping(CREATE_PASSWORD)
    // public ResponseEntity<GenericResponse> createPassword(@RequestHeader(name =
    // AUTH_USER_ID_HEADER, defaultValue = "0") Long userId) {
    // GenericResponse res = new GenericResponse();
    // res.setMessage(userId);
    // return ResponseEntity.ok(res);
    // }

    @PostMapping(CREATE_PASSWORD)
    public ResponseEntity<GenericResponse> createNewPassword(
            @RequestHeader(name = AUTH_USER_ID_HEADER, defaultValue = "0") Long userId,
            @Valid @RequestBody CreatePasswordRequest request,
            BindingResult bindingResult) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(passwordMapper.createNewPassword(userId, request, bindingResult));
    }
}
