package dev.twiceb.instanceservice.service.impl;

import java.time.Instant;
import java.util.ConcurrentModificationException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.twiceb.common.dto.request.AdminSignupRequest;
import dev.twiceb.common.dto.response.AdminTokenGrant;
import dev.twiceb.common.dto.response.TokenGrant;
import dev.twiceb.common.enums.AuthErrorCodes;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import dev.twiceb.common.exception.AuthException;
import dev.twiceb.instanceservice.client.UserClient;
import dev.twiceb.instanceservice.dto.request.InstanceConfigurationUpdateRequest;
import dev.twiceb.instanceservice.domain.model.Instance;
import dev.twiceb.instanceservice.domain.model.InstanceAdmin;
import dev.twiceb.instanceservice.domain.model.InstanceConfiguration;
import dev.twiceb.instanceservice.domain.repository.InstanceAdminRepository;
import dev.twiceb.instanceservice.domain.repository.InstanceConfigurationRepository;
import dev.twiceb.instanceservice.domain.repository.InstanceRepository;
import dev.twiceb.instanceservice.domain.projection.InstanceProjection;
import dev.twiceb.instanceservice.service.InstanceService;
import dev.twiceb.instanceservice.service.util.ConfigurationHelper;
import dev.twiceb.instanceservice.shared.ConfigKeyLookupRecord;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InstanceServiceImpl implements InstanceService {

    private static final Set<InstanceConfigurationKey> BOOL_KEYS = Set.of(
            InstanceConfigurationKey.ENABLE_SIGNUP,
            InstanceConfigurationKey.ENABLE_MAGIC_LINK_LOGIN,
            InstanceConfigurationKey.ENABLE_EMAIL_PASSWORD, InstanceConfigurationKey.ENABLE_SMTP,
            InstanceConfigurationKey.EMAIL_HOST, InstanceConfigurationKey.IS_GITHUB_ENABLED,
            InstanceConfigurationKey.IS_GITLAB_ENABLED, InstanceConfigurationKey.IS_GOOGLE_ENABLED);

    // keys that trigger version bump
    private static final Set<InstanceConfigurationKey> VERSIONED_KEYS = Set.copyOf(BOOL_KEYS);

    // clients;
    private final UserClient userClient;
    // repositories
    private final InstanceRepository instanceRepository;
    private final InstanceAdminRepository iAdminRepository;
    private final InstanceConfigurationRepository iConfigurationRepository;
    private final ConfigurationHelper cHelper;

    @Override
    @Transactional
    public InstanceProjection getInstancePrinciple() {
        return instanceRepository.findFirstByOrderByCreatedAtAsc(InstanceProjection.class)
                .orElse(null);
    }

    @Override
    public Map<InstanceConfigurationKey, String> getConfigurationValues() {
        Map<InstanceConfigurationKey, String> config = cHelper.getConfigurationValues(List.of(
                new ConfigKeyLookupRecord(InstanceConfigurationKey.ENABLE_SIGNUP, "0"),
                new ConfigKeyLookupRecord(InstanceConfigurationKey.IS_GOOGLE_ENABLED, "0"),
                new ConfigKeyLookupRecord(InstanceConfigurationKey.IS_GITHUB_ENABLED, "0"),
                new ConfigKeyLookupRecord(InstanceConfigurationKey.GITHUB_APP_NAME, ""),
                new ConfigKeyLookupRecord(InstanceConfigurationKey.IS_GITLAB_ENABLED, "0"),
                new ConfigKeyLookupRecord(InstanceConfigurationKey.EMAIL_HOST, ""),
                new ConfigKeyLookupRecord(InstanceConfigurationKey.ENABLE_MAGIC_LINK_LOGIN, "1"),
                new ConfigKeyLookupRecord(InstanceConfigurationKey.ENABLE_EMAIL_PASSWORD, "1")));
        return config;
    }

    @Override
    public long getInstanceVersion() {
        Instance instance =
                instanceRepository.findFirstByOrderByCreatedAtAsc(Instance.class).orElse(null);

        if (instance != null) {
            return instance.getConfigVersion();
        }

        return 0L;
    }

    @Override
    @Transactional
    public Map<InstanceConfigurationKey, String> updateConfigurations(
            InstanceConfigurationUpdateRequest request) {
        Map<InstanceConfigurationKey, String> updates = toUpdatesMap(request);
        List<InstanceConfiguration> iConfigs =
                iConfigurationRepository.findByKeyIn(updates.keySet());

        boolean bumpNeeded = false;
        for (InstanceConfiguration iConfig : iConfigs) {
            if (VERSIONED_KEYS.contains(iConfig.getKey())) {
                bumpNeeded = true;
            }
            iConfig.setValue(
                    iConfig.isEncrypted() ? cHelper.encryptValue(updates.get(iConfig.getKey()))
                            : updates.get(iConfig.getKey()));
        }

        if (bumpNeeded) {
            Long expected =
                    instanceRepository.findFirstConfigVersionByOrderByCreatedAtAsc().orElse(null);
            if (expected != null) {
                int bumped = instanceRepository.bumpConfigVersion(UUID.randomUUID(), expected,
                        Instant.now());
                if (bumped != 1) {
                    throw new ConcurrentModificationException("Config modified concurrently");
                }
            }
        }

        return updates;
    }

    @Override
    @Transactional
    public TokenGrant adminSignup(AdminSignupRequest payload) {
        // check if instance exist;
        Instance instance =
                instanceRepository.findFirstByOrderByCreatedAtAsc(Instance.class).orElse(null);
        if (instance == null) {
            throw new AuthException(AuthErrorCodes.INSTANCE_NOT_CONFIGURED);
        }

        // check if instance already has admin;
        UUID instanceAdminId = iAdminRepository.findFirstIdByOrderByCreatedAtAsc().orElse(null);
        if (instanceAdminId != null) {
            throw new AuthException(AuthErrorCodes.ADMIN_ALREADY_EXIST);
        }

        // return error if the email and password is not present

        // validate the email or do it on user-service
        String email = payload.getEmail().trim().trim();

        // check if already a user exists or not
        UUID userId = userClient.getUserIdByEmail(email);
        if (userId != null) {
            throw new AuthException(AuthErrorCodes.ADMIN_USER_ALREADY_EXIST,
                    Map.of("email", email, "first_name", payload.getFirstName(), "last_name",
                            payload.getLastName(), "company_name", payload.getCompanyName()));
        }

        AdminTokenGrant adminTokenGrant = userClient.createUser(payload);
        instance.finishSetup(payload.getCompanyName());
        instance = instanceRepository.save(instance);
        InstanceAdmin instanceAdmin = InstanceAdmin.create(instance, adminTokenGrant.getUserId());
        iAdminRepository.save(instanceAdmin);
        return adminTokenGrant.getTokenGrant();
    }

    private Map<InstanceConfigurationKey, String> toUpdatesMap(
            InstanceConfigurationUpdateRequest request) {
        // keep things ordered
        Map<InstanceConfigurationKey, String> map = new LinkedHashMap<>();

        putIfNotNull(map, InstanceConfigurationKey.ENABLE_SIGNUP, request.getENABLE_SIGNUP());
        putIfNotNull(map, InstanceConfigurationKey.ENABLE_MAGIC_LINK_LOGIN,
                request.getENABLE_MAGIC_LINK_LOGIN());
        putIfNotNull(map, InstanceConfigurationKey.ENABLE_EMAIL_PASSWORD,
                request.getENABLE_EMAIL_PASSWORD());
        putIfNotNull(map, InstanceConfigurationKey.IS_GOOGLE_ENABLED,
                request.getIS_GOOGLE_ENABLED());
        putIfNotNull(map, InstanceConfigurationKey.IS_GITHUB_ENABLED,
                request.getIS_GITHUB_ENABLED());
        putIfNotNull(map, InstanceConfigurationKey.GITHUB_APP_NAME, request.getGITHUB_APP_NAME());
        putIfNotNull(map, InstanceConfigurationKey.EMAIL_HOST, request.getEMAIL_HOST());

        for (Map.Entry<InstanceConfigurationKey, String> entry : map.entrySet()) {
            String normalized = normalizeValues(entry.getKey(), entry.getValue());
            entry.setValue(normalized);
        }

        return map;
    }

    private void putIfNotNull(Map<InstanceConfigurationKey, String> map,
            InstanceConfigurationKey key, String value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    private String normalizeValues(InstanceConfigurationKey key, String value) {
        if (!BOOL_KEYS.contains(key)) {
            return value;
        }

        if (value == null) {
            return "0";
        }

        if ("true".equalsIgnoreCase(value) || "1".equals(value)) {
            return "1";
        }

        if ("false".equalsIgnoreCase(value) || "0".equals(value)) {
            return "0";
        }

        throw new IllegalStateException("Configuration Key not found");
    }


}
