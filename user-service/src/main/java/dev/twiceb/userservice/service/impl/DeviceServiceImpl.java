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
import dev.twiceb.userservice.service.util.UserServiceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static dev.twiceb.common.constants.ErrorMessage.DEVICE_VERIFICATION_EXPIRED;
import static dev.twiceb.common.constants.PathConstants.*;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final UserDeviceRepository userDeviceRepository;
    private final ActivationCodeRepository activationCodeRepository;
    private final EmailService emailService;
    private final UserServiceHelper helper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public boolean verifyDevice(Long userId, String deviceKey) {
        return Optional.of(helper.decodeAndHashDeviceVerificationCode(deviceKey))
                .map(key -> userDeviceRepository.existsByHashedDeviceKey(key, userId))
                .orElse(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Optional<User> retrieveAndValidateDeviceVerificationCode(String deviceVerificationToken) {
        String hashedDeviceToken = helper.decodeAndHashDeviceVerificationCode(deviceVerificationToken);
        return retrieveActivationCode(hashedDeviceToken)
                .filter(this::validateActivationCode)
                .map(this::invalidateActivationCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public User processNewDevice(User user, String deviceKey) {
        String hashedDeviceKey = helper.decodeAndHashDeviceVerificationCode(deviceKey);
        return updateUserDevices(user, hashedDeviceKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void sendDeviceVerificationEmail(User user, Map<String, String> customHeaders) {
        String randomCode = helper.generateRandomCode();
        String hashedRandomCode = helper.decodeAndHashDeviceVerificationCode(randomCode);
        ActivationCode code = new ActivationCode(hashedRandomCode, ActivationCodeType.DEVICE_VERIFICATION, user);
        activationCodeRepository.save(code);

        emailService.sendDeviceVerificationEmail(user, randomCode,
                customHeaders.get(AUTH_USER_IP_HEADER), customHeaders.get(AUTH_USER_AGENT_HEADER));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public boolean isDeviceVerificationCodeSent(Long userId, LocalDateTime currentTime) {
        ActivationCode activationCode = getLatestDeviceVerificationCode(userId);

        if (activationCode == null) {
            return false;
        }

        if (isCodeUsed(activationCode)) {
            return false;
        }

        if (isCodeExpired(activationCode, currentTime)) {
            extendCodeExpiration(activationCode, currentTime.plusMinutes(5));
            return true;
        }

        return true;
    }

    private boolean validateActivationCode(ActivationCode ac) {
        if (ac == null) {
            throw new ApiRequestException("no ac found", HttpStatus.NOT_FOUND);
        } else if (ac.getCodeType().equals(ActivationCodeType.ACTIVATION)) {
            throw new ApiRequestException("bad token", HttpStatus.BAD_REQUEST);
        } else if (isCodeExpired(ac, LocalDateTime.now())) {
            throw new ApiRequestException(DEVICE_VERIFICATION_EXPIRED, HttpStatus.BAD_REQUEST);
        }
        return true;
    }

    private User invalidateActivationCode(ActivationCode ac) {
        ac.setExpirationTime(LocalDateTime.now());
        ac.setHashedCode("");
        ac = activationCodeRepository.save(ac);
        return ac.getUser();
    }

    private ActivationCode getLatestDeviceVerificationCode(Long userId) {
        return activationCodeRepository
                .findFirstByUser_IdAndCodeTypeOrderByExpirationTimeAsc(userId, ActivationCodeType.DEVICE_VERIFICATION)
                .orElse(null);
    }

    private boolean isCodeUsed(ActivationCode activationCode) {
        return activationCode.getHashedCode().isEmpty();
    }

    private boolean isCodeExpired(ActivationCode activationCode, LocalDateTime currentTime) {
        return currentTime.isAfter(activationCode.getExpirationTime());
    }

    private void extendCodeExpiration(ActivationCode activationCode, LocalDateTime newExpirationTime) {
        activationCode.setExpirationTime(newExpirationTime);
        activationCodeRepository.save(activationCode);
    }

    private Optional<ActivationCode> retrieveActivationCode(String hashedDeviceCode) {
        return activationCodeRepository.getActivationCodeByHashedCode(hashedDeviceCode, ActivationCode.class);
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
