package dev.twiceb.userservice.service;

import dev.twiceb.common.exception.AuthException;
import dev.twiceb.userservice.Credentials;
import dev.twiceb.userservice.model.User;

public interface AuthProvider {

    String provider(); // "email", "magicCode"

    /**
     * @param creds holds email/password or email/magicCode
     * @param isSignUp true for sign-up flow, false for sign-in
     */
    User authenticate(Credentials creds, boolean isSignUp) throws AuthException;
}
