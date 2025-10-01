package dev.twiceb.userservice.service;

import dev.twiceb.common.application.internal.bundle.IssuedTokens;
import dev.twiceb.userservice.domain.model.User;
import dev.twiceb.userservice.dto.internal.TokenProvenance;


public interface TokenService {
    public IssuedTokens issueTokens(User user, TokenProvenance provenance);

    public IssuedTokens rotateRefreshToken(String refreshToken, TokenProvenance provenance);
}
