package dev.twiceb.passwordservice.mapper;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import dev.twiceb.common.dto.response.GenericResponse;
import dev.twiceb.common.dto.response.HeaderResponse;
import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.passwordservice.dto.request.CreatePasswordRequest;
import dev.twiceb.passwordservice.dto.request.UpdatePasswordRequest;
import dev.twiceb.passwordservice.dto.response.AllPasswordsResponse;
import dev.twiceb.passwordservice.service.PasswordService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PasswordMapper {

    private final PasswordService passwordService;
    private final BasicMapper basicMapper;

    public GenericResponse createNewPassword(Long userId, CreatePasswordRequest request, BindingResult bindingResult) {
        return basicMapper.convertToResponse(passwordService.createPasswordForDomain(userId, request, bindingResult),
                GenericResponse.class);
    }

    public HeaderResponse<AllPasswordsResponse> getPasswords(Long userId, Pageable pageable) {
        return basicMapper.getHeaderResponse(passwordService.getPasswords(userId, pageable),
                AllPasswordsResponse.class);
    }

    public HeaderResponse<AllPasswordsResponse> getExpiringPasswords(Long userId, Pageable pageable) {
        return basicMapper.getHeaderResponse(passwordService.getExpiringPasswords(userId, pageable),
                AllPasswordsResponse.class);
    }

    public HeaderResponse<AllPasswordsResponse> getRecentPasswords(Long userId, Pageable pageable) {
        return basicMapper.getHeaderResponse(passwordService.getRecentPasswords(userId, pageable),
                AllPasswordsResponse.class);
    }

    public GenericResponse updatePasswordForDomain(Long userId, UpdatePasswordRequest request,
            BindingResult bindingResult) {
        return basicMapper.convertToResponse(passwordService.updatePasswordForDomain(userId, request, bindingResult),
                GenericResponse.class);
    }

    public GenericResponse getDecryptedPassword(Long userId, Long passwordId) {
        return basicMapper.convertToResponse(passwordService.getDecryptedPassword(userId, passwordId),
                GenericResponse.class);
    }

    public GenericResponse testing() {
        return basicMapper.convertToResponse(passwordService.testingStuff(),
                GenericResponse.class);
    }
}
