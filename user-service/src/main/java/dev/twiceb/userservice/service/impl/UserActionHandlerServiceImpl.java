package dev.twiceb.userservice.service.impl;

import org.springframework.stereotype.Service;
import dev.twiceb.common.event.PasswordChangeEvent;
// import dev.twiceb.userservice.domain.repository.LoginRepository;
// import dev.twiceb.userservice.domain.repository.UserDeviceRepository;
// import dev.twiceb.userservice.service.EmailService;
import dev.twiceb.userservice.service.UserActionHandlerService;
// import dev.twiceb.userservice.service.util.UserServiceHelper;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserActionHandlerServiceImpl implements UserActionHandlerService {

    // private final UserDeviceRepository userDeviceRepository;
    // private final LoginRepository loginAttemptRepository;
    // private final EmailService emailService;
    // private final UserServiceHelper helper;

    @Override
    public void handlePasswordChangeEvent(PasswordChangeEvent event, UUID authUserId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handlePasswordChangeEvent'");
    }



}
