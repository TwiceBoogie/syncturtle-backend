package dev.twiceb.userservice.mapper;

import dev.twiceb.common.dto.response.TokenGrant;
import dev.twiceb.common.enums.MagicCodeType;
import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.userservice.dto.request.AuthContextRequest;
import dev.twiceb.userservice.dto.request.AuthenticationRequest;
import dev.twiceb.userservice.dto.request.MagicCodeRequest;
import dev.twiceb.userservice.dto.request.RefreshTokenRequest;
import dev.twiceb.userservice.dto.response.AuthUserResponse;
import dev.twiceb.userservice.dto.response.AuthenticationResponse;
import dev.twiceb.userservice.dto.response.MagicCodeResponse;
import dev.twiceb.userservice.dto.response.MagicKeyResponse;
import dev.twiceb.userservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthenticationMapper {

    private final AuthenticationService authenticationService;
    private final BasicMapper mapper;

    public MagicCodeResponse checkEmail(MagicCodeRequest request) {
        return mapper.convertToResponse(

                authenticationService.checkEmail(request.getEmail()), MagicCodeResponse.class);
    }

    public MagicKeyResponse generateMagicCodeAuth(MagicCodeRequest request) {
        return mapper.convertToResponse(Map.of("key", authenticationService
                .generateMagicCode(request.getEmail(), MagicCodeType.MAGIC_LINK)),
                MagicKeyResponse.class);
    }

    public MagicKeyResponse generateMagicCodeDevice(MagicCodeRequest request) {
        return mapper
                .convertToResponse(
                        Map.of("key",
                                authenticationService.generateMagicCode(request.getEmail(),
                                        MagicCodeType.DEVICE_VERIFICATION)),
                        MagicKeyResponse.class);
    }

    public AuthenticationResponse login(AuthContextRequest<AuthenticationRequest> request) {
        return mapper.convertToResponse(authenticationService.login(request),
                AuthenticationResponse.class);
    }

    public AuthenticationResponse magicLogin(AuthContextRequest<MagicCodeRequest> request) {
        return mapper.convertToResponse(authenticationService.magicLogin(request),
                AuthenticationResponse.class);
    }

    public TokenGrant refreshToken(AuthContextRequest<RefreshTokenRequest> request) {
        return authenticationService.refreshToken(request);
    }

    AuthenticationResponse getAuthenticationResponse(Map<String, Object> credentials) {
        AuthenticationResponse response = new AuthenticationResponse();
        response.setUser(mapper.convertToResponse(credentials.get("user"), AuthUserResponse.class));
        response.setToken((String) credentials.get("token"));
        return response;
    }
}
