package dev.twiceb.instanceservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.twiceb.common.dto.internal.AuthAdminResult;
import dev.twiceb.common.dto.request.AdminSignupRequest;
import dev.twiceb.common.enums.AuthErrorCodes;
import dev.twiceb.common.enums.UserRole;
import dev.twiceb.common.exception.AuthException;
import dev.twiceb.common.repository.projections.OnlyId;
import dev.twiceb.instanceservice.client.UserClient;
import dev.twiceb.instanceservice.domain.model.Instance;
import dev.twiceb.instanceservice.domain.model.InstanceAdmin;
import dev.twiceb.instanceservice.domain.repository.InstanceAdminRepository;
import dev.twiceb.instanceservice.domain.repository.InstanceConfigurationRepository;
import dev.twiceb.instanceservice.domain.repository.InstanceRepository;
import dev.twiceb.instanceservice.service.InstanceService;
import dev.twiceb.instanceservice.service.impl.InstanceServiceImpl;
import dev.twiceb.instanceservice.service.util.AppProperties;
import dev.twiceb.instanceservice.service.util.ConfigurationHelper;
import dev.twiceb.instanceservice.service.util.AppProperties.ConfigKeys;

@ExtendWith(MockitoExtension.class)
public class InstanceServiceTest {

    private final UserClient userClient = mock(UserClient.class);
    private final InstanceRepository instanceRepository = mock(InstanceRepository.class);
    private final InstanceAdminRepository iAdminRepository = mock(InstanceAdminRepository.class);
    private final InstanceConfigurationRepository iConfigurationRepository = mock(
            InstanceConfigurationRepository.class);

    private AppProperties appProps;
    private ConfigurationHelper cHelper;

    private InstanceService instanceService;

    @BeforeEach
    void setup() {
        appProps = new AppProperties();
        appProps.setSecretKey("test-secret");
        appProps.setSkipEnvVar(true);
        appProps.setTest(true);

        ConfigKeys configKeys = new AppProperties.ConfigKeys();
        appProps.setConfigKeys(configKeys);

        StringEncryptor encryptor = new StringEncryptor() {

            @Override
            public String encrypt(String message) {
                return message == null ? "" : message;
            }

            @Override
            public String decrypt(String encryptedMessage) {
                return encryptedMessage == null ? "" : encryptedMessage;
            }

        };

        cHelper = new ConfigurationHelper(encryptor, iConfigurationRepository, appProps);
        instanceService = new InstanceServiceImpl(userClient, instanceRepository, iAdminRepository,
                iConfigurationRepository, cHelper, appProps);
    }

    @Test
    void adminSignup_CreatesAdmin_whenNoAdminExists() {
        // arrange
        Instance instance = Instance.register("0.0.1", "0.0.1", "sig", true);
        when(instanceRepository.findFirstByOrderByCreatedAtAsc(Instance.class)).thenReturn(Optional.of(instance));
        when(iAdminRepository.findFirstIdByOrderByCreatedAtAsc()).thenReturn(Optional.empty());
        when(userClient.getUserIdByEmail("admin@example.com")).thenReturn(null);

        UUID newUserId = UUID.randomUUID();
        when(userClient.createUser(any(AdminSignupRequest.class)))
                .thenReturn(new AuthAdminResult(newUserId, UserRole.ADMIN));

        AdminSignupRequest request = new AdminSignupRequest();
        request.setEmail("Admin@example.com");
        request.setFirstName("abc");
        request.setLastName("abc");
        request.setCompanyName("example");

        // act
        AuthAdminResult res = instanceService.adminSignup(request);

        // assert
        assertEquals(newUserId, res.getUserId());
        verify(iAdminRepository).save(any(InstanceAdmin.class));
        verify(instanceRepository, atLeastOnce()).save(any(Instance.class));
    }

    @Test
    void adminSignup_throwsWhenAdminExists() {
        // arrange
        Instance instance = Instance.register("0.0.1", "0.0.1", "sig", true);
        when(instanceRepository.findFirstByOrderByCreatedAtAsc(Instance.class)).thenReturn(Optional.of(instance));
        when(iAdminRepository.findFirstIdByOrderByCreatedAtAsc())
                .thenReturn(Optional.of((OnlyId) () -> UUID.randomUUID()));

        AdminSignupRequest request = new AdminSignupRequest();
        request.setEmail("admin@example.com");

        // act + assert
        AuthException ex = assertThrows(AuthException.class, () -> instanceService.adminSignup(request));
        assertEquals(AuthErrorCodes.ADMIN_ALREADY_EXIST, ex.getErrorCode());
        verifyNoInteractions(userClient);
    }
}
