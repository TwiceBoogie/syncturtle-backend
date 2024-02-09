package dev.twiceb.passwordservice.service.cron;

import dev.twiceb.common.dto.request.NotificationRequest;
import dev.twiceb.common.enums.TimePeriod;
import dev.twiceb.passwordservice.enums.DomainStatus;
import dev.twiceb.passwordservice.feign.NotificationClient;
import dev.twiceb.passwordservice.repository.KeychainRepository;
import dev.twiceb.passwordservice.repository.PasswordUpdateStatRepository;
import dev.twiceb.passwordservice.repository.projection.KeychainExpiringProjection;
import dev.twiceb.passwordservice.repository.projection.KeychainNotificationProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CronService {

    private final KeychainRepository keychainRepository;
    private final NotificationClient notificationClient;
    private final PasswordUpdateStatRepository passwordUpdateStatRepository;

    // every-day
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void sendExpiringSoonNotification() {
        updateKeychainStatusToSoon();
        updateKeychainStatusToExpired();
        List<KeychainNotificationProjection> keychains = keychainRepository.findAllNotNotificationSent();
        List<NotificationRequest> request = new ArrayList<>();
        List<Long> keychainIds = new ArrayList<>();
        keychains.forEach((keychain) -> {
            keychainIds.add(keychain.getId());

            NotificationRequest notificationRequest = new NotificationRequest();
            notificationRequest.setNotificationType("password");
            notificationRequest.setUserId(keychain.getUserId());
            notificationRequest.setMessage(createMessage(keychain.getDomain(), keychain.getExpiryDate()));

            request.add(notificationRequest);
        });

        notificationClient.sendBatchNotification(request);
        keychainRepository.updateMultiNotificationSent(keychainIds);
    }

    // every-week, so monday at 12am.
    @Scheduled(cron = "0 0 0 * * 1")
    @Transactional
    public void aggregateChangeLogByPolicyWeekly() {
        passwordUpdateStatRepository.callInsertUpdateStats(TimePeriod.WEEKLY);
    }

    // monthly, so the first day of the month
    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void aggregateChangeLogByPolicyMonthly() {
        passwordUpdateStatRepository.callInsertUpdateStats(TimePeriod.MONTHLY);
    }

    // yearly, so the first day of january
    @Scheduled(cron = "0 0 0 1 1 ?")
    @Transactional
    public void aggregateChangeLogByPolicyYearly() {
        passwordUpdateStatRepository.callInsertUpdateStats(TimePeriod.YEARLY);
    }

    @Transactional
    protected void updateKeychainStatusToSoon() {
        List<KeychainExpiringProjection> keychains = keychainRepository.findAllKeychainsByStatus(DomainStatus.ACTIVE);
        List<Long> keychainIds = new ArrayList<>();

        for (KeychainExpiringProjection entity : keychains) {
            LocalDate currentTime = LocalDate.now();
            int notificationDays = entity.getPolicy().getNotificationDays();
            Duration duration = Duration.ofDays(notificationDays);
            LocalDate notificationDate = entity.getExpiryDate().minus(duration);

            if (currentTime.equals(notificationDate)) {
                keychainIds.add(entity.getId());
            }
        }

        keychainRepository.updateKeychainStatus(DomainStatus.SOON, keychainIds);
    }

    @Transactional
    protected void updateKeychainStatusToExpired() {
        List<KeychainExpiringProjection> keychains = keychainRepository.findAllKeychainsByStatus(DomainStatus.SOON);
        List<Long> keychainIds = new ArrayList<>();

        for (KeychainExpiringProjection entity : keychains) {
            LocalDate currentTime = LocalDate.now();
            if (currentTime.isAfter(entity.getExpiryDate())) {
                keychainIds.add(entity.getId());
            }
        }

        keychainRepository.updateKeychainStatus(DomainStatus.EXPIRED, keychainIds);
    }

    private String createMessage(String domain, LocalDate expiryDate) {
        return String.format("Dear user, your password for %s will or has expired on %s", domain, expiryDate);
    }
}
