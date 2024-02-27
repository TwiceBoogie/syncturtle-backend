package dev.twiceb.userservice.service.impl;

import dev.twiceb.common.enums.UserStatus;
import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.userservice.enums.ActivationCodeType;
import dev.twiceb.userservice.model.ActivationCode;
import dev.twiceb.userservice.model.User;
import dev.twiceb.userservice.model.UserDevice;
import dev.twiceb.userservice.repository.ActivationCodeRepository;
import dev.twiceb.userservice.repository.UserDeviceRepository;
import dev.twiceb.userservice.service.DeviceService;
import dev.twiceb.userservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static dev.twiceb.common.constants.ErrorMessage.DEVICE_VERIFICATION_EXPIRED;
import static dev.twiceb.common.constants.PathConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final UserDeviceRepository userDeviceRepository;
    private final EmailService emailService;
    private final ActivationCodeRepository activationCodeRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public boolean verifyDevice(Long userId, String hashedDeviceKey) {
        return verifyHashedDevice(userId, hashedDeviceKey);
    }

    @Override
    @Transactional
    public User retrieveAndValidateDeviceVerificationCode(String hashedDeviceCode) {
        ActivationCode ac = retrieveActivationCode(hashedDeviceCode);
        ac = checkActivationCodeExpiration(ac); // and expire it
        return ac.getUser();
    }

    @Override
    @Transactional
    public User verifyNewDevice(User user, String deviceKey) {
        return updateUserDevices(user, deviceKey);
    }

    @Override
    @Transactional
    public void sendDeviceVerificationEmail(User user, String randomCode, String hashedRandomCode, Map<String, String> customHeaders) {
        ActivationCode code = new ActivationCode(hashedRandomCode, ActivationCodeType.DEVICE_VERIFICATION, user);
        activationCodeRepository.save(code);

        emailService.sendDeviceVerificationEmail(user, randomCode,
                customHeaders.get(AUTH_USER_IP_HEADER), customHeaders.get(AUTH_USER_AGENT_HEADER));
    }

    private ActivationCode retrieveActivationCode(String hashedDeviceCode) {
        return activationCodeRepository.getActivationCodeByHashedCode(
                hashedDeviceCode, ActivationCode.class
        ).orElseThrow(() -> new ApiRequestException("no ac found", HttpStatus.FORBIDDEN));
    }

    private ActivationCode checkActivationCodeExpiration(ActivationCode activationCode) {
        LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isAfter(activationCode.getExpirationTime())) {
            throw new ApiRequestException(DEVICE_VERIFICATION_EXPIRED, HttpStatus.BAD_REQUEST);
        }
        activationCode.setExpirationTime(currentTime);
        activationCode.setHashedCode("");
        return activationCodeRepository.save(activationCode);
    }

    private boolean verifyHashedDevice(Long userId, String hashedDeviceKey) {
        if (hashedDeviceKey == null || hashedDeviceKey.isEmpty()) return false;
        return userDeviceRepository.existsByHashedDeviceKey(hashedDeviceKey);
    }

    private User updateUserDevices(User user, String deviceKey) {
        user.setUserStatus(UserStatus.ACTIVE);
        UserDevice newDevice = new UserDevice();
        newDevice.setUser(user);
        newDevice.setDeviceName("trusted_device" + UUID.randomUUID());
        newDevice.setDeviceKey(deviceKey);
        user.getUserDevices().add(newDevice);
        return user;
    }
}
