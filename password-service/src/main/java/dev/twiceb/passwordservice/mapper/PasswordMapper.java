package dev.twiceb.passwordservice.mapper;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import dev.twiceb.common.dto.response.GenericResponse;
import dev.twiceb.common.dto.response.HeaderResponse;
import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.passwordservice.dto.request.CreatePasswordRequest;
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
}
