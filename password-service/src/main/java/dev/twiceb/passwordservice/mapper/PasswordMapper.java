package dev.twiceb.passwordservice.mapper;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.passwordservice.dto.request.*;
import dev.twiceb.passwordservice.dto.response.EncryptionKeysResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import dev.twiceb.common.dto.response.GenericResponse;
import dev.twiceb.common.dto.response.HeaderResponse;
import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.passwordservice.dto.response.PasswordsResponse;
import dev.twiceb.passwordservice.service.PasswordService;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PasswordMapper {

    private final PasswordService passwordService;
    private final BasicMapper basicMapper;

    public GenericResponse createNewPassword(CreatePasswordRequest request, BindingResult bindingResult) {
        return basicMapper.convertToResponse(passwordService.createNewPassword(request, bindingResult),
                GenericResponse.class);
    }

    public GenericResponse updatePasswordOnly(UUID passwordId, UpdatePasswordRequest request,
                                              BindingResult bindingResult) {
        return basicMapper.convertToResponse(
                passwordService.updatePasswordOnly(passwordId, request.getPassword(), bindingResult),
                GenericResponse.class);
    }

    public GenericResponse updateUsername(UUID passwordId, UpdatePasswordRequest request, BindingResult bindingResult) {
        return basicMapper.convertToResponse(passwordService.updateUsername(
                passwordId, request.getUsername(), bindingResult), GenericResponse.class);
    }

    public GenericResponse updatePasswordNotes(UUID passwordId, UpdatePasswordRequest request,
                                               BindingResult bindingResult) {
        return basicMapper.convertToResponse(passwordService.updatePasswordNotes(
                passwordId, request.getNotes(), bindingResult), GenericResponse.class);
    }

    public void updateTagsOnPassword(UUID passwordId, UpdatePasswordRequest request,
                                     BindingResult bindingResult) {
        passwordService.updateTagsOnPassword(passwordId, request.getTags(), bindingResult);
    }

    public void favoritePassword(UUID passwordId, UpdatePasswordRequest request, BindingResult bindingResult) {
        passwordService.favoritePassword(passwordId, request.isFavorite(), bindingResult);
    }

    public HeaderResponse<PasswordsResponse> getPasswords(Pageable pageable) {
        return basicMapper.getHeaderResponse(passwordService.getPasswords(pageable),
                PasswordsResponse.class);
    }

    public PasswordsResponse getPassword(UUID keychainId) {
        return basicMapper.convertToResponse(passwordService.getPassword(keychainId), PasswordsResponse.class);
    }

    public HeaderResponse<PasswordsResponse> getPasswordsByCriteria(String criteria, Pageable pageable) {
        return switch (criteria) {
            case "expiring" ->
                    basicMapper.getHeaderResponse(
                            passwordService.getExpiringPasswords(pageable), PasswordsResponse.class
                    );
            case "recent" -> basicMapper.getHeaderResponse(
                    passwordService.getRecentPasswords(pageable), PasswordsResponse.class
            );
            default -> throw new ApiRequestException("Criteria doesn't exist.", HttpStatus.BAD_REQUEST);
        };
    }

    public GenericResponse getDecryptedPassword(UUID passwordId) {
        return basicMapper.convertToResponse(passwordService.getDecryptedPassword(passwordId),
                GenericResponse.class);
    }

    public GenericResponse generateRandomPassword(int length) {
        return basicMapper.convertToResponse(passwordService.generateSecurePassword(length), GenericResponse.class);
    }

    public GenericResponse deletePassword(UUID passwordId) {
        return basicMapper.convertToResponse(passwordService.deletePassword(passwordId), GenericResponse.class);
    }

    public GenericResponse deleteAllPasswords() {
        return basicMapper.convertToResponse(passwordService.deleteAllPasswords(), GenericResponse.class);
    }

    public HeaderResponse<PasswordsResponse> searchPasswordsByQuery(SearchQueryRequest request,
                                                                    BindingResult bindingResult, Pageable pageable) {
        return basicMapper.getHeaderResponse(passwordService.searchPasswordsByQuery(
                request, bindingResult, pageable), PasswordsResponse.class
        );
    }

    public HeaderResponse<EncryptionKeysResponse> getEncryptionKeys(Pageable pageable) {
        return basicMapper.getHeaderResponse(passwordService.getEncryptionKeys(pageable), EncryptionKeysResponse.class);
    }
}
