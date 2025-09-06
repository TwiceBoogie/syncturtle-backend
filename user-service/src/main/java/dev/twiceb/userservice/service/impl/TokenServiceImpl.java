package dev.twiceb.userservice.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.twiceb.common.dto.response.RefreshCookie;
import dev.twiceb.common.dto.response.TokenGrant;
import dev.twiceb.common.enums.AuthErrorCodes;
import dev.twiceb.common.exception.AuthException;
import dev.twiceb.common.security.JwtProvider;
import dev.twiceb.common.security.JwtProvider.AccessToken;
import dev.twiceb.userservice.domain.model.RefreshToken;
import dev.twiceb.userservice.domain.model.User;
import dev.twiceb.userservice.domain.repository.RefreshTokenRepository;
import dev.twiceb.userservice.dto.internal.TokenProvenance;
import dev.twiceb.userservice.service.TokenService;
import dev.twiceb.userservice.service.security.BcryptHasher;
import dev.twiceb.userservice.utils.TokenGenerator;
import dev.twiceb.userservice.utils.TokenGenerator.TokenPair;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final RefreshTokenRepository rTokenRepository;
    private final JwtProvider jwtProvider;
    private final BcryptHasher hasher;

    @Override
    @Transactional
    public TokenGrant mint(User user, TokenProvenance provenance) {
        // 1: create TokenPair(handle, secret); create RefreshToken; set secret
        TokenPair pair = TokenGenerator.newPair();
        RefreshToken rt = RefreshToken.issue(user, pair, provenance);
        rt.setSecret(hasher.hash(pair.secret()));
        // 2: save token
        rt = rTokenRepository.save(rt);

        // 3: create access token
        AccessToken at = jwtProvider.createAccessToken(user.getId(), null);
        RefreshCookie rc =
                new RefreshCookie(refreshCookie(pair.handle(), pair.secret()), rt.getExpiresAt());

        return new TokenGrant(at, rc);
    }

    @Override
    @Transactional
    public TokenGrant rotate(String refreshToken, TokenProvenance provenance) {
        // step 1: split and validate token
        String[] parts = refreshToken.split("\\.", 2);
        String currentHandle = parts[0];
        String currentSecret = parts[1];
        if (parts.length != 2) {
            throw new AuthException(AuthErrorCodes.EXPIRED_PASSWORD_TOKEN);
        }

        // step 2: find token and validate
        RefreshToken rtCurrent = rTokenRepository.findByHandle(currentHandle).orElseThrow();
        if (!rtCurrent.isValid(provenance.getNow())) {
            throw new AuthException(AuthErrorCodes.EXPIRED_PASSWORD_TOKEN);
        }

        if (!hasher.matches(currentSecret, rtCurrent.getSecretHash())) {
            throw new AuthException(AuthErrorCodes.EXPIRED_PASSWORD_TOKEN);
        }

        // step 3: create new token, replacing old one
        TokenPair pair = TokenGenerator.newPair();
        RefreshToken rtNext = rtCurrent.rotate(pair, provenance);
        rtNext.setSecret(hasher.hash(pair.secret()));

        // 4: save new token
        rtNext = rTokenRepository.save(rtNext);

        // 5: mint AccessToken and return
        AccessToken at = jwtProvider.createAccessToken(rtNext.getUser().getId(), null);
        RefreshCookie rc = new RefreshCookie(refreshCookie(pair.handle(), pair.secret()),
                rtNext.getExpiresAt());

        return new TokenGrant(at, rc);
    }

    private String refreshCookie(String handle, String secret) {
        return handle + "." + secret;
    }

}
