package dev.twiceb.userservice.service.impl;

import dev.twiceb.userservice.repository.UserRepository;
import dev.twiceb.userservice.service.UserClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserClientServiceImpl implements UserClientService {

    private final UserRepository userRepository;

    @Override
    public String getUserEmail(Long userId) {
        return userRepository.getUserEmail(userId);
    }

    @Override
    public void increaseNotificationCount(Long userId) {
        userRepository.increaseNotificationCount(userId);
    }

    @Override
    public void decreaseNotificationCount(Long userId) {
        userRepository.decreaseNotificationCount(userId);
    }

    @Override
    public void resetNotificationCount(Long userId) {
        userRepository.resetNotificationCount(userId);
    }
}
