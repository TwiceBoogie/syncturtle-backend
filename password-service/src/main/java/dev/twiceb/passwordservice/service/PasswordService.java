package dev.twiceb.passwordservice.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import dev.twiceb.passwordservice.dto.request.CreatePasswordRequest;
import dev.twiceb.passwordservice.dto.request.UpdatePasswordRequest;
import dev.twiceb.passwordservice.repository.projection.UserPasswordProjection;

public interface PasswordService {

    Map<String, String> createPasswordForDomain(Long userId, CreatePasswordRequest request,
            BindingResult bindingResult);

    Page<UserPasswordProjection> getPasswords(Pageable pageable);

    Page<UserPasswordProjection> getExpiringPasswords(Pageable pageable);

    Page<UserPasswordProjection> getRecentPasswords(Pageable pageable);

    Map<String, String> updatePasswordForDomain(UpdatePasswordRequest request, BindingResult bindingResult);

    Map<String, String> getDecryptedPassword(Long passwordId);

}
