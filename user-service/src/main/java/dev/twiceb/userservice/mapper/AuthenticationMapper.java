package dev.twiceb.userservice.mapper;

import dev.twiceb.common.dto.response.GenericResponse;
import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.userservice.dto.request.AuthenticationRequest;
import dev.twiceb.userservice.dto.request.MagicCodeRequest;
import dev.twiceb.userservice.dto.request.PasswordResetRequest;
import dev.twiceb.userservice.dto.response.AuthUserResponse;
import dev.twiceb.userservice.dto.response.AuthenticationResponse;
import dev.twiceb.userservice.dto.response.MagicCodeResponse;
import dev.twiceb.userservice.dto.response.MagicKeyResponse;
import dev.twiceb.userservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.Map;

@Component @RequiredArgsConstructor
public class AuthenticationMapper {

    private final AuthenticationService authenticationService;
    private final BasicMapper mapper;

    public MagicCodeResponse checkEmail(MagicCodeRequest request, BindingResult bindingResult){
        return mapper.convertToResponse(
                authenticationService.checkEmail(request.getEmail(),bindingResult),
                MagicCodeResponse.class);
    }

    public MagicKeyResponse generateMagicCode(MagicCodeRequest request,
            BindingResult bindingResult){
        return mapper.convertToResponse(
                Map.of("key",
                        authenticationService.generateMagicCode(request.getEmail(),bindingResult)),
                MagicKeyResponse.class);
    }

    public AuthenticationResponse login(AuthenticationRequest request, BindingResult bindingResult){
        return mapper.convertToResponse(authenticationService.login(request,bindingResult),
                AuthenticationResponse.class);
    }

    public AuthenticationResponse magicLogin(MagicCodeRequest request, BindingResult bindingResult){
        return mapper.convertToResponse(authenticationService.magicLogin(request,bindingResult),
                AuthenticationResponse.class);
    }

    public AuthenticationResponse getUserByToken(){
        return getAuthenticationResponse(authenticationService.getUserByToken());
    }

    public GenericResponse forgotUsername(String email, BindingResult bindingResult){
        return mapper.convertToResponse(authenticationService.forgotUsername(email,bindingResult),
                GenericResponse.class);
    }

    public GenericResponse forgotPassword(String email, BindingResult bindingResult){
        return mapper.convertToResponse(authenticationService.forgotPassword(email,bindingResult),
                GenericResponse.class);
    }

    public GenericResponse verifyOtp(String otp, BindingResult bindingResult){
        return mapper.convertToResponse(authenticationService.verifyOtp(otp,bindingResult),
                GenericResponse.class);
    }

    public GenericResponse resetPassword(PasswordResetRequest request, String token,
            BindingResult bindingResult){
        return mapper.convertToResponse(
                authenticationService.resetPassword(request,token,bindingResult),
                GenericResponse.class);
    }

    public AuthenticationResponse verifyDeviceVerification(String token, boolean trust){
        return mapper.convertToResponse(authenticationService.newDeviceVerification(token,trust),
                AuthenticationResponse.class);
    }

    AuthenticationResponse getAuthenticationResponse(Map<String, Object> credentials){
        AuthenticationResponse response = new AuthenticationResponse();
        response.setUser(mapper.convertToResponse(credentials.get("user"),AuthUserResponse.class));
        response.setToken((String) credentials.get("token"));
        return response;
    }
}
