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
import lombok.SneakyThrows;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmqpConsumer {

    private final UserRepository userRepository;
    private final EnvelopeEncryption envelopeEncryption;
    private final RotationPolicyRepository rotationPolicyRepository;

    @SneakyThrows
    @RabbitListener(queues = "q.passwordsvc")
    @Transactional
    public void userCreatedListener(UserPrincipleResponse res) {
        if (userRepository.existsById(res.getId())) {
            log.error("Error from user-svc to password-svc", new RuntimeException("User already exists"));
        }

        User user = new User(res.getId(), res.getFullName(), res.getUsername(), res.getUserStatus(), res.getRole());
        SecretKey randomKey = envelopeEncryption.generateKey();
        RotationPolicy rotationPolicy = rotationPolicyRepository.findById(2L).get();

        EncryptionKey key = new EncryptionKey(
                user,
                Base64.getEncoder().encodeToString(randomKey.getEncoded()),
                "Default",
                randomKey.getAlgorithm(),
                envelopeEncryption.getHighSecurityKeySize(),
                rotationPolicy
        );
        key.setDescription("Default encryption key");
        user.getEncryptionKeys().add(key);
        userRepository.save(user);
    }
}
