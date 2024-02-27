package dev.twiceb.userservice.service;

import dev.twiceb.userservice.model.User;

import java.util.Map;

public interface DeviceService {

    boolean verifyDevice(Long userId, String hashedDeviceKey);

    User retrieveAndValidateDeviceVerificationCode(String deviceVerificationCode);
    User verifyNewDevice(User user, String deviceKey);
    void sendDeviceVerificationEmail(User user, String randomCode, String hashedRandomCode, Map<String, String> customHeaders);
}
