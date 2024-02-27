package dev.twiceb.passwordservice.service.cron;

import dev.twiceb.common.dto.request.NotificationRequest;
import dev.twiceb.common.enums.TimePeriod;
import dev.twiceb.common.util.EnvelopeEncryption;
import dev.twiceb.passwordservice.enums.DomainStatus;
import dev.twiceb.passwordservice.feign.NotificationClient;
import dev.twiceb.passwordservice.model.EncryptionKey;
import dev.twiceb.passwordservice.model.Keychain;
import dev.twiceb.passwordservice.repository.EncryptionKeyRepository;
import dev.twiceb.passwordservice.repository.KeychainRepository;
import dev.twiceb.passwordservice.repository.PasswordUpdateStatRepository;
import dev.twiceb.passwordservice.repository.projection.KeychainExpiringProjection;
import dev.twiceb.passwordservice.repository.projection.KeychainNotificationProjection;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CronService {

    private static final int BATCH_SIZE = 100;

    private final KeychainRepository keychainRepository;
    private final EncryptionKeyRepository encryptionKeyRepository;
    private final NotificationClient notificationClient;
    private final PasswordUpdateStatRepository passwordUpdateStatRepository;
    private final EnvelopeEncryption envelopeEncryption;

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

    // every day at 1am, encryptionKey will be rotated if it has expired.
    @Scheduled(cron = "0 0 0 1 */3 ?")
    @Transactional
    public void rotateEncryptionKeys() {
        int pageNumber = 0;
        Pageable pageable = PageRequest.of(pageNumber, BATCH_SIZE);
        Page<EncryptionKey> page;
        LocalDateTime currentTime = LocalDateTime.now();

        do {
            page = encryptionKeyRepository.findAllByExpirationDateAfter(currentTime, pageable);
            if (page.isEmpty()) {
                return;
            }
            // Process the current batch of encryption keys
            List<EncryptionKey> updatedKeys = processEncryptionKeys(page.getContent());
            encryptionKeyRepository.saveAll(updatedKeys);

            pageNumber++;
            pageable = PageRequest.of(pageNumber, BATCH_SIZE);
        } while (page.hasNext());
    }

    @SneakyThrows
    private List<EncryptionKey> processEncryptionKeys(List<EncryptionKey> encryptionKeys) {
        for (EncryptionKey key : encryptionKeys) {
            byte[] decodedBytesSecretKey = Base64.getDecoder().decode(key.getDek());
            SecretKey oldSecretKey = new SecretKeySpec(decodedBytesSecretKey, key.getAlgorithm());
            SecretKey newSecretKey = envelopeEncryption.generateKey();
            key.setDek(Base64.getEncoder().encodeToString(newSecretKey.getEncoded()));
            key.setModifiedAt(LocalDateTime.now());

            for (Keychain keychain : key.getKeychains()) {
                IvParameterSpec oldVector = envelopeEncryption.regenerateIvFromBytes(keychain.getVector());
                String decryptedPassword = envelopeEncryption.decrypt(keychain.getPassword(), oldSecretKey, oldVector);

                IvParameterSpec newVector = envelopeEncryption.generateIv();
                byte[] encryptedPassword = envelopeEncryption.encrypt(decryptedPassword, newSecretKey, newVector);
                keychain.setPassword(encryptedPassword);
                keychain.setVector(newVector.getIV());
                keychain.setEncryptionKey(key);
            }
        }
        return encryptionKeys;
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
