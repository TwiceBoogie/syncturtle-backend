package dev.twiceb.passwordservice.controller.rest;

import static dev.twiceb.common.constants.ErrorMessage.DOMAIN_ALREADY_EXIST;
import static dev.twiceb.common.constants.ErrorMessage.NO_RESOURCE_FOUND;
import static dev.twiceb.common.constants.ErrorMessage.SAME_SAVED_PASSWORD;
import static dev.twiceb.common.constants.ErrorMessage.UNAUTHORIZED;
import static dev.twiceb.common.constants.PathConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

import dev.twiceb.passwordservice.repository.KeychainRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;
import java.util.Optional;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import dev.twiceb.passwordservice.model.EncryptionKey;
import dev.twiceb.passwordservice.model.Keychain;
import dev.twiceb.passwordservice.repository.EncryptionKeyRepository;
import dev.twiceb.passwordservice.service.util.PasswordHelperService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.twiceb.common.util.EnvelopeEncryption;
import dev.twiceb.common.util.TestConstants;
import dev.twiceb.passwordservice.dto.request.CreatePasswordRequest;
import dev.twiceb.passwordservice.dto.request.UpdatePasswordRequest;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = {"/sql-test/clear-password-db.sql",
        "/sql-test/populate-password-db.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql-test/clear-password-db.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class PasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private EnvelopeEncryption envelopeEncryption;

    @Autowired
    private PasswordHelperService helper;

    @Autowired
    private EncryptionKeyRepository encryptionKeyRepository;

    @Autowired
    private KeychainRepository keychainRepository;

    @Test
    @DisplayName("testing envelope encryption")
    public void testingEncryption() throws Exception {
//                SecretKey randomKey = envelopeEncryption.generateKey();
//                assertNotNull(randomKey, "Key should not be null");
//                String encodedString = Base64.getEncoder().encodeToString(randomKey.getEncoded());
//                System.out.println(encodedString);
        // Retrieve the encryption key from the repository
        Optional<EncryptionKey> ekOpt = encryptionKeyRepository.findById(2L);
        assertThat(ekOpt).isPresent();
        EncryptionKey ek = ekOpt.get();

        Keychain keychain = keychainRepository.findById(2L).get();


        // Rebuild the SecretKey using the helper method
        SecretKey secretKey = helper.rebuildSecretKey(ek.getDek(), ek.getAlgorithm());

        // Generate a new initialization vector (IV)
        IvParameterSpec vector = helper.generateNewIv();

        // Encrypt the password
        String originalPassword = "Twice_Momo1";
        byte[] encryptedPassword = helper.encryptPassword(originalPassword, secretKey, vector);

        // Encode the encrypted password and IV to Base64 for storage or transmission
        String encryptedPasswordBase64 = Base64.getEncoder().encodeToString(encryptedPassword);
        String vectorBase64 = Base64.getEncoder().encodeToString(vector.getIV());
        assertArrayEquals(encryptedPassword, Base64.getDecoder().decode(encryptedPasswordBase64));
        // Decode the Base64 encoded IV for decryption
        byte[] decodedVector = Base64.getDecoder().decode(vectorBase64);
        assertArrayEquals(encryptedPassword, keychain.getPassword(), "the encrypted password bytes should be equal");
        // Decrypt the password
        String decryptedPassword = helper.decryptPassword(keychain.getPassword(), secretKey, keychain.getVector());
        String decryptedPassword2 = helper.decryptPassword(encryptedPassword, secretKey, decodedVector);

        // Assert that the decrypted password matches the original password
        assertEquals(originalPassword, decryptedPassword, "Decrypted password should match the original password");

    }

    @Test
    @DisplayName("[201] POST /ui/v1/password - Create new domain password")
    public void createNewPassword() throws Exception {
        CreatePasswordRequest request = new CreatePasswordRequest();
        request.setEncryptionId(TestConstants.ENCRYPTION_ID);
        request.setDomain(TestConstants.DOMAIN);
        request.setWebsiteUrl(TestConstants.WEBSITE_URL);
        request.setUsername(TestConstants.DOMAIN_USERNAME);
        request.setPassword(TestConstants.DOMAIN_PASSWORD);
        request.setConfirmPassword(TestConstants.DOMAIN_CONFIRM_PASSWORD);
        request.setNotes(TestConstants.DOMAIN_NOTES);
        request.setPasswordExpiryPolicy(TestConstants.DOMAIN_EXPIRY_POLICY);
        request.setCategory(TestConstants.DOMAIN_CATEGORIES);
        mockMvc.perform(post(UI_V1_PASSWORD).header(AUTH_USER_ID_HEADER,
                                TestConstants.USER_ID)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is("Password saved for " +
                        TestConstants.DOMAIN)));
    }

    @Test
    @DisplayName("[401] POST /ui/v1/password - Encryption key wrong ownership")
    public void createNewPassword_ShouldUserNotOwnKey() throws Exception {
        CreatePasswordRequest request = new CreatePasswordRequest();
        request.setEncryptionId(TestConstants.ENCRYPTION_ID);
        request.setDomain(TestConstants.DOMAIN);
        request.setWebsiteUrl(TestConstants.WEBSITE_URL);
        request.setUsername(TestConstants.DOMAIN_USERNAME);
        request.setPassword(TestConstants.DOMAIN_PASSWORD);
        request.setConfirmPassword(TestConstants.DOMAIN_CONFIRM_PASSWORD);
        request.setNotes(TestConstants.DOMAIN_NOTES);
        request.setPasswordExpiryPolicy(TestConstants.DOMAIN_EXPIRY_POLICY);
        request.setCategory(TestConstants.DOMAIN_CATEGORIES);
        mockMvc.perform(post(UI_V1_PASSWORD).header(AUTH_USER_ID_HEADER, 1l)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is(UNAUTHORIZED)));
    }

    @Test
    @DisplayName("[404] POST /ui/v1/password - Encryption key not found")
    public void createNewPassword_ShouldEncryptionKeyNotFound() throws Exception {
        CreatePasswordRequest request = new CreatePasswordRequest();
        request.setEncryptionId(50L);
        request.setDomain(TestConstants.DOMAIN);
        request.setWebsiteUrl(TestConstants.WEBSITE_URL);
        request.setUsername(TestConstants.DOMAIN_USERNAME);
        request.setPassword(TestConstants.DOMAIN_PASSWORD);
        request.setConfirmPassword(TestConstants.DOMAIN_CONFIRM_PASSWORD);
        request.setNotes(TestConstants.DOMAIN_NOTES);
        request.setPasswordExpiryPolicy(TestConstants.DOMAIN_EXPIRY_POLICY);
        request.setCategory(TestConstants.DOMAIN_CATEGORIES);
        mockMvc.perform(post(UI_V1_PASSWORD).header(AUTH_USER_ID_HEADER, TestConstants.USER_ID)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(NO_RESOURCE_FOUND)));
    }

    @Test
    @DisplayName("[400] POST /ui/v1/password - Domain already exist")
    public void createNewPassword_ShouldDomainExist() throws Exception {
        CreatePasswordRequest request = new CreatePasswordRequest();
        request.setEncryptionId(TestConstants.ENCRYPTION_ID);
        request.setDomain(TestConstants.SAME_DOMAIN);
        request.setWebsiteUrl(TestConstants.SAME_WEBSITE_URL);
        request.setUsername(TestConstants.DOMAIN_USERNAME);
        request.setPassword(TestConstants.DOMAIN_PASSWORD);
        request.setConfirmPassword(TestConstants.DOMAIN_CONFIRM_PASSWORD);
        request.setNotes(TestConstants.DOMAIN_NOTES);
        request.setPasswordExpiryPolicy(TestConstants.DOMAIN_EXPIRY_POLICY);
        request.setCategory(TestConstants.DOMAIN_CATEGORIES);
        mockMvc.perform(post(UI_V1_PASSWORD).header(AUTH_USER_ID_HEADER, TestConstants.USER_ID)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(DOMAIN_ALREADY_EXIST)));
    }

    @Test
    @DisplayName("[200] PATCH /ui/v1/password/update/{passwordId} - Update password only")
    public void updatePasswordOnly() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setPassword("Twice_Momo1");
        request.setConfirmPassword("Twice_Momo1");
        Long passwordId = 1L;
        mockMvc.perform(patch(UI_V1_PASSWORD + UPDATE_PASSWORD, passwordId)
                        .header(AUTH_USER_ID_HEADER, TestConstants.USER_ID)
                        .header(AUTH_DEVICE_KEY_ID, 1L)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Password updated successfully.")));
    }

    @Test
    @DisplayName("[404] PATCH /ui/v1/password/update/{passwordId} - Password not found")
    public void updatePasswordOnly_ShouldPasswordNotFound() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setPassword("Twice_Momo1");
        request.setConfirmPassword("Twice_Momo1");
        Long passwordId = 50L;
        mockMvc.perform(patch(UI_V1_PASSWORD + UPDATE_PASSWORD, passwordId)
                        .header(AUTH_USER_ID_HEADER, TestConstants.USER_ID)
                        .header(AUTH_DEVICE_KEY_ID, 1L)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(NO_RESOURCE_FOUND)));
    }

    @Test
    @DisplayName("[401] PATCH /ui/v1/password/update/{passwordId} - Password doesn't belong to user")
    public void updatePasswordOnly_ShouldPasswordNotBelongToUser() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setPassword("Twice_Momo1");
        request.setConfirmPassword("Twice_Momo1");
        Long passwordId = 1L;
        mockMvc.perform(patch(UI_V1_PASSWORD + UPDATE_PASSWORD, passwordId)
                        .header(AUTH_USER_ID_HEADER, 3L)
                        .header(AUTH_DEVICE_KEY_ID, 2L)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is(UNAUTHORIZED)));
    }

    @Test
    @DisplayName("[409] PATCH /ui/v1/password/update/{passwordId} - New password is the same as the one in db")
    public void updatePasswordOnly_ShouldNewPasswordMatchDbOne() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setPassword("Twice_Momo1");
        request.setConfirmPassword("Twice_Momo1");
        Long passwordId = 2L;
        mockMvc.perform(patch(UI_V1_PASSWORD + UPDATE_PASSWORD, passwordId)
                        .header(AUTH_USER_ID_HEADER, 3L)
                        .header(AUTH_DEVICE_KEY_ID, 2L)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is(SAME_SAVED_PASSWORD)));
    }

    @Test
    @DisplayName("[204] PUT /ui/v1/password/tags/{passwordId} - Update password tags")
    public void updateTags() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setTags(TestConstants.DOMAIN_CATEGORIES);
        Long passwordId = 2L;
        mockMvc.perform(put(UI_V1_PASSWORD + UPDATE_PASSWORD_TAGS, passwordId)
                        .header(AUTH_USER_ID_HEADER, 3L)
                        .header(AUTH_DEVICE_KEY_ID, 2L)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("[404] PUT /ui/v1/password/tags/{passwordId} - No password found using the id")
    public void updateTags_ShouldPasswordNotFound() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setTags(TestConstants.DOMAIN_CATEGORIES);
        Long passwordId = 60L;
        mockMvc.perform(put(UI_V1_PASSWORD + UPDATE_PASSWORD_TAGS, passwordId)
                        .header(AUTH_USER_ID_HEADER, 3L)
                        .header(AUTH_DEVICE_KEY_ID, 2L)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(NO_RESOURCE_FOUND)));
    }

    @Test
    @DisplayName("[401] PUT /ui/v1/password/tags/{passwordId} - Non valid tags")
    public void updateTags_ShouldTagsNotValid() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setTags(TestConstants.INVALID_DOMAIN_CATEGORIES);
        Long passwordId = 2L;
        mockMvc.perform(put(UI_V1_PASSWORD + UPDATE_PASSWORD_TAGS, passwordId)
                        .header(AUTH_USER_ID_HEADER, 3L)
                        .header(AUTH_DEVICE_KEY_ID, 2L)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[201] PUT /ui/v1/password/favorite/{passwordId} - Update password favorite status")
    public void favoritePassword() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setFavorite(true);
        Long passwordId = 2L;
        mockMvc.perform(put(UI_V1_PASSWORD + FAVORITE_PASSWORD, passwordId)
                        .header(AUTH_USER_ID_HEADER, 3L)
                        .header(AUTH_DEVICE_KEY_ID, 2L)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("[404] PUT /ui/v1/password/favorite/{passwordId} - Password not found")
    public void favoritePassword_ShouldPasswordNotFound() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setFavorite(true);
        Long passwordId = 50L;
        mockMvc.perform(put(UI_V1_PASSWORD + FAVORITE_PASSWORD, passwordId)
                        .header(AUTH_USER_ID_HEADER, 3L)
                        .header(AUTH_DEVICE_KEY_ID, 2L)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(NO_RESOURCE_FOUND)));
    }

    @Test
    @DisplayName("[200] PATCH /ui/v1/password/username/update/{passwordId} - Update username on password")
    public void updateUsername() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setUsername("randomUsername");
        Long passwordId = 2L;
        mockMvc.perform(patch(UI_V1_PASSWORD + UPDATE_PASSWORD_USERNAME, passwordId)
                        .header(AUTH_USER_ID_HEADER, 3L)
                        .header(AUTH_DEVICE_KEY_ID, 2L)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Username updated successfully.")));
    }

    @Test
    @DisplayName("[401] PATCH /ui/v1/password/username/update/{passwordId} - Password not found")
    public void updateUsername_ShouldPasswordNotFound() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setUsername("randomUsername");
        Long passwordId = 50L;
        mockMvc.perform(patch(UI_V1_PASSWORD + UPDATE_PASSWORD_USERNAME, passwordId)
                        .header(AUTH_USER_ID_HEADER, 3L)
                        .header(AUTH_DEVICE_KEY_ID, 2L)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(NO_RESOURCE_FOUND)));
    }

    @Test
    @DisplayName("[200] GET /ui/v1/password - Get user passwords")
    public void getPasswords() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        mockMvc.perform(get(UI_V1_PASSWORD)
                        .header(AUTH_USER_ID_HEADER, 3L)
                        .header(AUTH_DEVICE_KEY_ID, 2L)
                        .content(mapper.writeValueAsString(pageable))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("[200] GET /ui/v1/password/{criteria} - Get user passwords with criteria (expiring)")
    public void getPasswords_CriteriaExpiring() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        mockMvc.perform(get(UI_V1_PASSWORD, "expiring")
                        .header(AUTH_USER_ID_HEADER, 3L)
                        .header(AUTH_DEVICE_KEY_ID, 2L)
                        .content(mapper.writeValueAsString(pageable))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("[200] GET /ui/v1/password/{criteria} - Get user passwords with criteria (recent)")
    public void getPasswords_CriteriaRecent() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        mockMvc.perform(get(UI_V1_PASSWORD, "recent")
                        .header(AUTH_USER_ID_HEADER, 3L)
                        .header(AUTH_DEVICE_KEY_ID, 2L)
                        .content(mapper.writeValueAsString(pageable))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("[200] GET /ui/v1/password/{passwordId}/info - Get password info")
    public void getPassword() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Long passwordId = 2L;
        mockMvc.perform(get(UI_V1_PASSWORD + GET_PASSWORD_INFO, passwordId)
                        .header(AUTH_USER_ID_HEADER, 3L)
                        .header(AUTH_DEVICE_KEY_ID, 2L)
                        .content(mapper.writeValueAsString(pageable))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.username", is("testuser1")));
    }

    @Test
    @DisplayName("[200] GET /ui/v1/password/decrypt/{passwordId} - Get decrypted password")
    public void getPassword_Decrypt() throws Exception {
        Long passwordId = 2L;
        mockMvc.perform(get(UI_V1_PASSWORD + GET_DECRYPTED_PASSWORD, passwordId)
                        .header(AUTH_USER_ID_HEADER, 3L)
                        .header(AUTH_DEVICE_KEY_ID, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Twice_Momo1")));
    }

    @Test
    @DisplayName("[401] GET /ui/v1/password/decrypt/{passwordId} - Password not found")
    public void getPassword_DecryptNotFound() throws Exception {
        Long passwordId = 50L;
        mockMvc.perform(get(UI_V1_PASSWORD + GET_DECRYPTED_PASSWORD, passwordId)
                        .header(AUTH_USER_ID_HEADER, 3L)
                        .header(AUTH_DEVICE_KEY_ID, 2L))
                .andExpect(status().isNotFound());
    }
}
