package dev.twiceb.passwordservice.mapper;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.passwordservice.dto.request.GenerateRandomPasswordRequest;
import dev.twiceb.passwordservice.dto.request.SearchQueryRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import dev.twiceb.common.dto.response.GenericResponse;
import dev.twiceb.common.dto.response.HeaderResponse;
import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.passwordservice.dto.request.CreatePasswordRequest;
import dev.twiceb.passwordservice.dto.request.UpdatePasswordRequest;
import dev.twiceb.passwordservice.dto.response.PasswordsResponse;
import dev.twiceb.passwordservice.service.PasswordService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PasswordMapper {

    private final PasswordService passwordService;
    private final BasicMapper basicMapper;

    public GenericResponse createNewPassword(Long userId, CreatePasswordRequest request, BindingResult bindingResult) {
        return basicMapper.convertToResponse(passwordService.createNewPassword(userId, request, bindingResult),
                GenericResponse.class);
    }

    public HeaderResponse<PasswordsResponse> getPasswords(Long userId, Pageable pageable) {
        return basicMapper.getHeaderResponse(passwordService.getPasswords(userId, pageable),
                PasswordsResponse.class);
    }

    public HeaderResponse<PasswordsResponse> getPasswordsByCriteria(Long userId, String criteria, Pageable pageable) {
        return switch (criteria) {
            case "expiring" ->
                    basicMapper.getHeaderResponse(
                            passwordService.getExpiringPasswords(userId, pageable), PasswordsResponse.class
                    );
            case "recent" -> basicMapper.getHeaderResponse(
                    passwordService.getRecentPasswords(userId, pageable), PasswordsResponse.class
            );
            default -> throw new ApiRequestException("Criteria doesn't exist.", HttpStatus.BAD_REQUEST);
        };
    }

    public GenericResponse updatePassword(Long userId, UpdatePasswordRequest request,
            BindingResult bindingResult) {
        return basicMapper.convertToResponse(passwordService.updatePassword(userId, request, bindingResult),
                GenericResponse.class);
    }

    public GenericResponse getDecryptedPassword(Long userId, Long passwordId) {
        return basicMapper.convertToResponse(passwordService.getDecryptedPassword(userId, passwordId),
                GenericResponse.class);
    }

    public GenericResponse generateRandomPassword(int length) {
        return basicMapper.convertToResponse(passwordService.generateSecurePassword(length), GenericResponse.class);
    }

    public GenericResponse deletePassword(Long userId, Long passwordId) {
        return basicMapper.convertToResponse(passwordService.deletePassword(userId, passwordId), GenericResponse.class);
    }

    public GenericResponse deleteAllPasswords(Long userId) {
        return basicMapper.convertToResponse(passwordService.deleteAllPasswords(userId), GenericResponse.class);
    }

    public HeaderResponse<PasswordsResponse> searchPasswordsByQuery(Long userId, SearchQueryRequest request,
                                                                    BindingResult bindingResult, Pageable pageable) {
        return basicMapper.getHeaderResponse(passwordService.searchPasswordsByQuery(
                userId, request, bindingResult, pageable), PasswordsResponse.class
        );
    }
}
