package dev.twiceb.passwordservice.amqp;

import dev.twiceb.common.util.EnvelopeEncryption;
import dev.twiceb.passwordservice.model.EncryptionKey;
import dev.twiceb.passwordservice.model.RotationPolicy;
import dev.twiceb.passwordservice.repository.RotationPolicyRepository;
import dev.twiceb.passwordservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import dev.twiceb.common.dto.response.UserPrincipleResponse;
import dev.twiceb.passwordservice.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmqpConsumer {

    private final UserRepository userRepository;
    private final EnvelopeEncryption envelopeEncryption;
    private final RotationPolicyRepository rotationPolicyRepository;

    @RabbitListener(queues = "q.passwordsvc")
    @Transactional
    public void userCreatedListener(UserPrincipleResponse res) {
        Optional.ofNullable(res).filter(response -> !userRepository.existsById(response.getId()))
                .ifPresentOrElse(this::processUser,
                        () -> log.error("User with ID {} already exists", res != null ? res.getId() : "unknown"));
    }

    private void processUser(UserPrincipleResponse res) {
        try {
            User user = new User(res.getId(), res.getFullName(), res.getUsername(), res.getUserStatus(), res.getRole());
            SecretKey randomKey = envelopeEncryption.generateKey();
            RotationPolicy rotationPolicy = rotationPolicyRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Rotation Policy not found"));
            String base64Encoded = Base64.getEncoder().encodeToString(randomKey.getEncoded());
            byte[] decodedBytes = Base64.getDecoder().decode(base64Encoded);
            log.info("Original Key Length: {}", randomKey.getEncoded().length);
            log.info("Decoded Key Length: {}", decodedBytes.length);
            EncryptionKey key = new EncryptionKey(
                    user,
                    Base64.getEncoder().encodeToString(randomKey.getEncoded()),
                    "Default",
                    randomKey.getAlgorithm(),
                    envelopeEncryption.getHighSecurityKeySize(),
                    rotationPolicy);
            key.setDescription("Default encryption key");
            user.getEncryptionKeys().add(key);
            userRepository.save(user);
        } catch (Exception e) {
            log.error("Error processing user creation for ID {}", res.getId(), e);
            throw new RuntimeException("Error processing user creation", e);
        }
    }
}
