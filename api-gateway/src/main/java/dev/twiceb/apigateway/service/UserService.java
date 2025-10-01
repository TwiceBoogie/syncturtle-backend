package dev.twiceb.apigateway.service;

import java.util.UUID;

import dev.twiceb.common.dto.response.UserPrincipalResponse;
import reactor.core.publisher.Mono;

/**
 * UserService Interface
 *
 * The primary goal of this interface is to abstract the interactions with the user-service,
 * reducing tight coupling and improving the maintainability and scalability of the application.
 */
public interface UserService {

    /**
     * Retrieves the user details for the given Id, either from cache or by making a call to the
     * user-service if the data is not cached or has expired. Ths method is essential for endpoints
     * that require authentication.
     * 
     * @param userId The user's Id used to retrieve the authenticated user.
     * @return {@link UserPrincipalResponse} containing the user's details
     */
    Mono<UserPrincipalResponse> getCachedUserDetails(UUID userId);

    /**
     * Retieves the ID of the valid user device asociated with the given email and deviceKey.
     *
     * @param email The user's email used to call getCachedUserDetails() to retrieve the list of
     *        user's devices.
     * @param deviceKey The device key insde the jwt to verify if it exist in user's device list.
     * @return The id of the valid user device, or null if the device key is not valid.
     */
    Mono<UUID> getValidUserDeviceId(UserPrincipalResponse user, String deviceKey);

}
