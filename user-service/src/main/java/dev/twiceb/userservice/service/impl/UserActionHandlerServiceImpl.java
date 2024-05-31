package dev.twiceb.userservice.service.impl;

import dev.twiceb.userservice.repository.projection.LoginAttemptProjection;
import dev.twiceb.userservice.service.LoginAttemptService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.twiceb.common.enums.ActionType;
import dev.twiceb.common.event.PasswordChangeEvent;
import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.userservice.model.UserAction;
import dev.twiceb.userservice.model.UserDevice;
import dev.twiceb.userservice.repository.UserDeviceRepository;
import dev.twiceb.userservice.service.EmailService;
import dev.twiceb.userservice.service.UserActionHandlerService;
import dev.twiceb.userservice.service.util.UserServiceHelper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserActionHandlerServiceImpl implements UserActionHandlerService {

    private final UserDeviceRepository userDeviceRepository;
    private final LoginAttemptService loginAttemptService;
    private final EmailService emailService;
    private final UserServiceHelper helper;

    @Override
    @Transactional
    public void handlePasswordChangeEvent(PasswordChangeEvent event, Long authUserId) {
        userDeviceRepository.findById(event.getDeviceKeyId())
                .filter(userDevice -> isDeviceOwnedByUser(userDevice, authUserId))
                .map(userDevice -> generateUserActionAndSendEmail(userDevice, event))
                .orElseThrow(() -> new ApiRequestException("something"));
    }

    private boolean isDeviceOwnedByUser(UserDevice device, Long authUserId) {
        return device.getUser().getId().equals(authUserId);
    }

    private UserDevice generateUserActionAndSendEmail(UserDevice device, PasswordChangeEvent event) {
        UserAction action = new UserAction();
        action.setUser(device.getUser());
        action.setActionType(ActionType.PASSWORD_CHANGE);
        action.setUserDevice(device);
        if (device.getUser().isNotifyPasswordChange()) {
            action.setUserNotified(true);
            String verificationCode = helper.generateRandomCode();
            // TODO: change name of the method to be more generic
            action.setVerificationCode(helper.decodeAndHashDeviceVerificationCode(verificationCode));
            action.setExpirationTime(event.getExpirationTime());
            LoginAttemptProjection loginAttempt = loginAttemptService.getRecentLoginAttempt(device.getUser().getId());
            emailService.sendPasswordChangeNotificationEmail(device, verificationCode,
                    event.getExpirationTime(), loginAttempt.getIpAddress());
        }
        device.getUserActionHistory().add(action);
        return userDeviceRepository.save(device);
    }

}
