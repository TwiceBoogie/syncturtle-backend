package dev.twiceb.passwordservice.controller.rest;

import dev.twiceb.common.dto.response.GenericResponse;
import dev.twiceb.common.dto.response.HeaderResponse;
import dev.twiceb.passwordservice.dto.request.CreatePasswordRequest;
import dev.twiceb.passwordservice.dto.response.PasswordsResponse;
import dev.twiceb.passwordservice.mapper.PasswordMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static dev.twiceb.common.constants.PathConstants.*;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping(UI_V1_PASSWORD)
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordMapper passwordMapper;

    @PostMapping
    public ResponseEntity<GenericResponse> createNewPassword(
            @RequestHeader(name = AUTH_USER_ID_HEADER, defaultValue = "0") Long userId,
            @Valid @RequestBody CreatePasswordRequest request,
            BindingResult bindingResult) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(passwordMapper.createNewPassword(userId, request, bindingResult));
    }

    @GetMapping
    public ResponseEntity<List<PasswordsResponse>> getPasswords(
            @RequestHeader(name = AUTH_USER_ID_HEADER, defaultValue = "0") Long userId,
            @PageableDefault(size = 10) Pageable Pageable) {
        HeaderResponse<PasswordsResponse> res = passwordMapper.getPasswords(userId, Pageable);
        return ResponseEntity.ok().headers(res.getHeaders()).body(res.getItems());
    }

    @GetMapping(GET_PASSWORD_WITH_CRITERIA)
    public ResponseEntity<List<PasswordsResponse>> getPasswordsByCriteria(
            @RequestHeader(name = AUTH_USER_ID_HEADER, defaultValue = "0") Long userId,
            @PathVariable("criteria") String criteria,
            @PageableDefault(size = 10) Pageable Pageable
    ) {
        HeaderResponse<PasswordsResponse> res = passwordMapper.getPasswordsByCriteria(userId, criteria, Pageable);
        return ResponseEntity.ok().headers(res.getHeaders()).body(res.getItems());
    }

    @GetMapping("password/test")
    public ResponseEntity<GenericResponse> testing() {
        return ResponseEntity.ok(passwordMapper.testing());
    }

}
