package dev.twiceb.userservice.service;

import dev.twiceb.common.enums.AuthMedium;
import dev.twiceb.common.exception.AuthException;
import dev.twiceb.userservice.utils.TokenGenerator.TokenPair;

public interface AuthInitiator {
    AuthMedium provider();

    TokenPair initiate(String email) throws AuthException;
}
