package dev.twiceb.userservice.service;

import dev.twiceb.common.dto.response.TokenGrant;
import dev.twiceb.userservice.domain.model.User;
import dev.twiceb.userservice.dto.internal.TokenProvenance;


public interface TokenService {
    public TokenGrant mint(User user, TokenProvenance provenance);

    public TokenGrant rotate(String refreshToken, TokenProvenance provenance);
}
