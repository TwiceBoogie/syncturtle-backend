package dev.twiceb.passwordservice.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Base64;
import java.util.Optional;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.transaction.annotation.Transactional;

import dev.twiceb.common.util.EnvelopeEncryption;
import dev.twiceb.passwordservice.model.EncryptionKey;
import dev.twiceb.passwordservice.model.RotationPolicy;
import dev.twiceb.passwordservice.model.User;
import dev.twiceb.passwordservice.repository.EncryptionKeyRepository;
import dev.twiceb.passwordservice.repository.RotationPolicyRepository;
import dev.twiceb.passwordservice.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = { "/sql-test/clear-password-db.sql",
        "/sql-test/populate-password-db.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = { "/sql-test/clear-password-db.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class EncryptionKeyDbTest {

    @Autowired
    private EncryptionKeyRepository encryptionKeyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnvelopeEncryption envelopeEncryption;

    @Autowired
    private RotationPolicyRepository rotationPolicyRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Transactional
    @DisplayName("testing encryptionKey transit to vault")
    public void testingRepo() throws Exception {
        // Ensure the user exists in the database
        Optional<User> userOpt = userRepository.findById(2L);
        assertThat(userOpt).isPresent();
        User user = userOpt.get();

        // Initialize the lazy collection

        // Generate encryption key and find rotation policy
        SecretKey randomKey = envelopeEncryption.generateKey();
        RotationPolicy rotationPolicy = rotationPolicyRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Rotation Policy not found"));

        IvParameterSpec vector = envelopeEncryption.generateIv();
        byte[] encryptedPassword = envelopeEncryption.encrypt("Twice_Mina1", randomKey, vector);
        System.out
                .println("Base64 Encoded Encrypted Password: " + Base64.getEncoder().encodeToString(encryptedPassword));
        System.out.println("Base64 Encoded Vector: " + Base64.getEncoder().encodeToString(vector.getIV()));

        // Create new encryption key and save it
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

        // Fetch the saved encryption key
        Optional<EncryptionKey> savedKeyOpt = encryptionKeyRepository.findById(key.getId());
        assertThat(savedKeyOpt).isPresent();
        EncryptionKey savedKey = savedKeyOpt.get();

        // Verify the saved key
        assertThat(savedKey.getUser()).isEqualTo(user);
        assertThat(savedKey.getName()).isEqualTo("Default");
        assertThat(savedKey.getAlgorithm()).isEqualTo(randomKey.getAlgorithm());
        assertThat(savedKey.getKeySize()).isEqualTo(envelopeEncryption.getHighSecurityKeySize());
        assertThat(savedKey.getRotationPolicy()).isEqualTo(rotationPolicy);

        // Directly query the database to check the dek value
        String sql = "SELECT dek FROM encryption_key WHERE id = ?";
        String dekFromDb = jdbcTemplate.queryForObject(sql, new Object[] { savedKey.getId() }, String.class);

        // Verify the raw value in the database
        System.out.println("Encrypted DEK in DB: " + dekFromDb);

        // Verify encryption and decryption
        String decryptedKey = savedKey.getDek();
        System.out.println(decryptedKey);
        assertThat(decryptedKey).isEqualTo(Base64.getEncoder().encodeToString(randomKey.getEncoded()));
    }

}
