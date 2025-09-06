package dev.twiceb.userservice.service.impl;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.records.DeviceRequestMetadata;
import dev.twiceb.userservice.domain.enums.ActivationCodeType;
import dev.twiceb.userservice.domain.model.ActivationCode;
import dev.twiceb.userservice.domain.model.User;
import dev.twiceb.userservice.domain.repository.ActivationCodeRepository;
import dev.twiceb.userservice.domain.repository.UserDeviceRepository;
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
    public boolean verifyDevice(UUID userId, String deviceKey) {
        return Optional.of(helper.decodeAndHashDeviceVerificationCode(deviceKey))
                .map(key -> userDeviceRepository.existsByHashedDeviceKey(key, userId))
                .orElse(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Optional<User> retrieveAndValidateDeviceVerificationCode(
            String deviceVerificationToken) {
        String hashedDeviceToken =
                helper.decodeAndHashDeviceVerificationCode(deviceVerificationToken);
        return retrieveActivationCode(hashedDeviceToken).filter(this::validateActivationCode)
                .map(this::invalidateActivationCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void sendDeviceVerificationEmail(User user, DeviceRequestMetadata metadata) {
        String randomCode = helper.generateRandomCode();
        String hashedRandomCode = helper.decodeAndHashDeviceVerificationCode(randomCode);
        ActivationCode code =
                new ActivationCode(hashedRandomCode, ActivationCodeType.DEVICE_VERIFICATION, user);
        activationCodeRepository.save(code);

        emailService.sendDeviceVerificationEmail(user, randomCode, metadata.ipAddress(),
                metadata.userAgent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public boolean isDeviceVerificationCodeSent(UUID userId, LocalDateTime currentTime) {
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

    private ActivationCode getLatestDeviceVerificationCode(UUID userId) {
        return activationCodeRepository.findFirstByUser_IdAndCodeTypeOrderByExpirationTimeAsc(
                userId, ActivationCodeType.DEVICE_VERIFICATION).orElse(null);
    }

    private boolean isCodeUsed(ActivationCode activationCode) {
        return activationCode.getHashedCode().isEmpty();
    }

    private boolean isCodeExpired(ActivationCode activationCode, LocalDateTime currentTime) {
        return currentTime.isAfter(activationCode.getExpirationTime());
    }

    private void extendCodeExpiration(ActivationCode activationCode,
            LocalDateTime newExpirationTime) {
        activationCode.setExpirationTime(newExpirationTime);
        activationCodeRepository.save(activationCode);
    }

    private Optional<ActivationCode> retrieveActivationCode(String hashedDeviceCode) {
        return activationCodeRepository.getActivationCodeByHashedCode(hashedDeviceCode,
                ActivationCode.class);
    }

    @Override
    public User processNewDevice(User user, String deviceKey) {
        throw new UnsupportedOperationException("Unimplemented method 'processNewDevice'");
    }

    // private User updateUserDevices(User user, String deviceKey) {
    // user.setUserStatus(UserStatus.ACTIVE);
    // UserDevice newDevice = new UserDevice();
    // newDevice.setUser(user);
    // newDevice.setDeviceName("trusted_device" + UUID.randomUUID());
    // newDevice.setDeviceKey(deviceKey);
    // user.getUserDevices().add(newDevice);
    // return user;
    // }
}
