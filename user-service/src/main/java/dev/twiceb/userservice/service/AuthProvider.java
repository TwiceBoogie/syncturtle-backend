package dev.twiceb.userservice.service;

import dev.twiceb.common.enums.AuthMedium;
import dev.twiceb.common.exception.AuthException;
import dev.twiceb.userservice.domain.model.User;

/**
 * Authentication provider contract (e.g, "email", "magic-code", etc)
 */
public interface AuthProvider {

    /**
     * Returns the name of the authentication provider.
     * 
     * @return the provider name (e.g, {@code "email"}, {@code "magicCode"})
     */
    public AuthMedium provider(); // "email", "magicCode"

    /**
     * Authenticates a user using the provided credentials.
     * 
     * @param key the key can either be ${{@code email}} or {@code magic_} + ${{@code email}}
     * @param code either the password or magic token
     * @param isSignup {@code true} for sign-up flow, {@code false} for sign-in
     * @return a saved/updated {@link User} entity from db
     */
    public User authenticate(String key, String code, boolean isSignup) throws AuthException;
}
