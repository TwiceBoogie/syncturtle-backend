package dev.twiceb.passwordservice.controller.rest;

import dev.twiceb.common.dto.response.GenericResponse;
import dev.twiceb.common.dto.response.HeaderResponse;
import dev.twiceb.passwordservice.dto.request.*;
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
            @Valid @RequestBody CreatePasswordRequest request,
            BindingResult bindingResult) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(passwordMapper.createNewPassword(request, bindingResult));
    }

    @PatchMapping(UPDATE_PASSWORD)
    public ResponseEntity<GenericResponse> updatePasswordOnly(
            @PathVariable("passwordId") Long passwordId,
            @Valid @RequestBody UpdatePasswordRequest request,
            BindingResult bindingResult
    ) {
        return ResponseEntity.ok(passwordMapper.updatePasswordOnly(passwordId, request, bindingResult));
    }

    @PatchMapping(UPDATE_PASSWORD_USERNAME)
    public ResponseEntity<GenericResponse> updateUsername(
            @PathVariable("passwordId") Long passwordId,
            @Valid @RequestBody UpdatePasswordRequest request,
            BindingResult bindingResult
    ) {
        return ResponseEntity.ok(passwordMapper.updateUsername(passwordId, request, bindingResult));
    }

    @PatchMapping(UPDATE_PASSWORD_NOTES)
    public ResponseEntity<GenericResponse> updatePasswordNotes(
            @PathVariable("passwordId") Long passwordId,
            @Valid @RequestBody UpdatePasswordRequest request,
            BindingResult bindingResult
    ) {
        return ResponseEntity.ok(passwordMapper.updatePasswordNotes(passwordId, request, bindingResult));
    }

    @PutMapping("/tags/{passwordId}")
    public ResponseEntity<Void> updateTagsOnPassword(
            @Valid @RequestBody UpdatePasswordRequest request,
            @PathVariable("passwordId") Long passwordId,
            BindingResult bindingResult
    ) {
        passwordMapper.updateTagsOnPassword(passwordId, request, bindingResult);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(FAVORITE_PASSWORD)
    public ResponseEntity<Void> favoritePassword(
            @Valid @RequestBody UpdatePasswordRequest request,
            @PathVariable("passwordId") Long passwordId,
            BindingResult bindingResult
    ) {
        passwordMapper.favoritePassword(passwordId, request, bindingResult);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<PasswordsResponse>> getPasswords(
            @PageableDefault(size = 10) Pageable Pageable) {
        HeaderResponse<PasswordsResponse> res = passwordMapper.getPasswords(Pageable);
        return ResponseEntity.ok().headers(res.getHeaders()).body(res.getItems());
    }

    @GetMapping(GET_PASSWORD_INFO)
    public ResponseEntity<PasswordsResponse> getPasswordInfo(@PathVariable("keychainId") Long keychainId) {
        return ResponseEntity.ok().body(passwordMapper.getPassword(keychainId));
    }

    @GetMapping(GET_PASSWORD_WITH_CRITERIA)
    public ResponseEntity<List<PasswordsResponse>> getPasswordsByCriteria(
            @PathVariable("criteria") String criteria,
            @PageableDefault(size = 10) Pageable Pageable
    ) {
        HeaderResponse<PasswordsResponse> res = passwordMapper.getPasswordsByCriteria(criteria, Pageable);
        return ResponseEntity.ok().headers(res.getHeaders()).body(res.getItems());
    }

    @GetMapping(GET_DECRYPTED_PASSWORD)
    public ResponseEntity<GenericResponse> getDecryptedPassword(@PathVariable("passwordId") Long passwordId) {
        return ResponseEntity.ok(passwordMapper.getDecryptedPassword(passwordId));
    }

    @DeleteMapping(DELETE_PASSWORD)
    public ResponseEntity<GenericResponse> deletePassword(@PathVariable("passwordId") Long passwordId) {
        return ResponseEntity.ok(passwordMapper.deletePassword(passwordId));
    }

    @DeleteMapping(DELETE_ALL)
    public ResponseEntity<GenericResponse> deleteAllPasswords() {
        return ResponseEntity.ok(passwordMapper.deleteAllPasswords());
    }

    @GetMapping(GENERATE_RANDOM_PASSWORD)
    public ResponseEntity<GenericResponse> generateRandomPassword(@PathVariable("length") int length) {
        return ResponseEntity.ok(passwordMapper.generateRandomPassword(length));
    }

    @PostMapping(SEARCH_BY_QUERY)
    public ResponseEntity<List<PasswordsResponse>> searchPasswordsByQuery(
            @Valid @RequestBody SearchQueryRequest request,
            BindingResult bindingResult,
            @PageableDefault(size = 10) Pageable Pageable
    ) {
        HeaderResponse<PasswordsResponse> res = passwordMapper.searchPasswordsByQuery(request, bindingResult, Pageable);
        return ResponseEntity.ok().headers(res.getHeaders()).body(res.getItems());
    }
}
