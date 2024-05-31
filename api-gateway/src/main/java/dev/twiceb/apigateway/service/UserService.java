package dev.twiceb.apigateway.service;

import dev.twiceb.common.dto.response.UserPrincipleResponse;

/**
 * UserService Interface
 *
 * The primary goal of this interface is to abstract the interactions with the
 * user-service,
 * reducing tight coupling and improving the maintainability and scalability of
 * the application.
 */
public interface UserService {

    /**
     * Retrieves the user details for the given email, either from cache
     * or by making a call to the user-service if the data is not cached or has
     * expired.
     * Ths method is essential for endpoints that require authentication.
     * 
     * @param email The user's email used to retrieve the authenticated user.
     * @return {@link UserPrincipleResponse} containing the user's details
     */
    UserPrincipleResponse getCachedUserDetails(String email);

    /**
     * Retieves the ID of the valid user device asociated with the given email and
     * deviceKey.
     *
     * @param email     The user's email used to call getCachedUserDetails() to
     *                  retrieve the list of user's devices.
     * @param deviceKey The device key insde the jwt to verify if it exist in user's
     *                  device list.
     * @return The id of the valid user device, or null if the device key is not
     *         valid.
     */
    Long getValidUserDeviceId(UserPrincipleResponse user, String deviceKey);

}
