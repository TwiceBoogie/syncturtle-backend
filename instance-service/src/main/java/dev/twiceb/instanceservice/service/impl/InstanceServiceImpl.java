package dev.twiceb.instanceservice.service.impl;

import static dev.twiceb.common.util.StringHelper.normalizeEmail;
import java.time.Instant;
import java.util.ConcurrentModificationException;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.twiceb.common.dto.internal.AuthAdminResult;
import dev.twiceb.common.dto.request.AdminSignupRequest;
import dev.twiceb.common.dto.response.InstanceStatusResult;
import dev.twiceb.common.enums.AuthErrorCodes;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.exception.AuthException;
import dev.twiceb.instanceservice.client.UserClient;
import dev.twiceb.instanceservice.dto.request.InstanceConfigurationUpdateRequest;
import dev.twiceb.instanceservice.dto.request.InstanceInfoUpdateRequest;
import dev.twiceb.instanceservice.dto.response.InstanceConfigurationResponse;
import dev.twiceb.instanceservice.domain.model.Instance;
import dev.twiceb.instanceservice.domain.model.InstanceAdmin;
import dev.twiceb.instanceservice.domain.model.InstanceConfiguration;
import dev.twiceb.instanceservice.domain.repository.InstanceAdminRepository;
import dev.twiceb.instanceservice.domain.repository.InstanceConfigurationRepository;
import dev.twiceb.instanceservice.domain.repository.InstanceRepository;
import dev.twiceb.instanceservice.domain.projection.OnlyId;
import dev.twiceb.instanceservice.domain.projection.InstanceAdminProjection;
import dev.twiceb.instanceservice.domain.projection.InstanceConfigVersionProjection;
import dev.twiceb.instanceservice.domain.projection.InstanceProjection;
import dev.twiceb.instanceservice.domain.projection.InstanceStatusProjection;
import dev.twiceb.instanceservice.service.InstanceService;
import dev.twiceb.instanceservice.service.util.AppProperties;
import dev.twiceb.instanceservice.service.util.ConfigurationHelper;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstanceServiceImpl implements InstanceService {

    private static final Set<InstanceConfigurationKey> BOOL_KEYS = Set.of(
            InstanceConfigurationKey.ENABLE_SIGNUP,
            InstanceConfigurationKey.ENABLE_MAGIC_LINK_LOGIN,
            InstanceConfigurationKey.DISABLE_WORKSPACE_CREATION,
            InstanceConfigurationKey.ENABLE_EMAIL_PASSWORD, InstanceConfigurationKey.ENABLE_SMTP,
            InstanceConfigurationKey.EMAIL_HOST, InstanceConfigurationKey.IS_GITHUB_ENABLED,
            InstanceConfigurationKey.IS_GITLAB_ENABLED, InstanceConfigurationKey.IS_GOOGLE_ENABLED);

    // keys that trigger version bump
    private static final Set<InstanceConfigurationKey> VERSIONED_KEYS = Set.copyOf(BOOL_KEYS);

    private static final Set<InstanceConfigurationKey> MANAGED_KEYS = EnumSet.of(
            // auth / workspace
            InstanceConfigurationKey.ENABLE_SIGNUP, InstanceConfigurationKey.ENABLE_EMAIL_PASSWORD,
            InstanceConfigurationKey.ENABLE_MAGIC_LINK_LOGIN,

            // smtp
            InstanceConfigurationKey.ENABLE_SMTP, InstanceConfigurationKey.EMAIL_HOST,

            // derived flags
            InstanceConfigurationKey.IS_GOOGLE_ENABLED, InstanceConfigurationKey.IS_GITHUB_ENABLED,
            InstanceConfigurationKey.IS_GITLAB_ENABLED);

    // clients;
    private final UserClient userClient;
    // repositories
    private final InstanceRepository instanceRepository;
    private final InstanceAdminRepository iAdminRepository;
    private final InstanceConfigurationRepository iConfigurationRepository;
    private final ConfigurationHelper cHelper;
    private final AppProperties appProperties;

    @Override
    @Transactional
    public InstanceProjection getInstanceInfo() {
        return instanceRepository.findFirstByOrderByCreatedAtAsc(InstanceProjection.class)
                .orElseThrow(
                        () -> new ApiRequestException("INSTANCE_NOT_SETUP", HttpStatus.BAD_REQUEST,
                                Map.of("isActivated", false, "isSetupDone", false)));

    }

    @Override
    @Transactional
    public InstanceProjection updateInstanceInfo(InstanceInfoUpdateRequest request) {
        Instance instance =
                instanceRepository.findFirstByOrderByCreatedAtAsc(Instance.class).orElseThrow();
        instance.updateInfo(request.getInstanceName(), request.getDomain(), request.getNamespace());
        instanceRepository.save(instance);
        return instanceRepository.findFirstByOrderByCreatedAtAsc(InstanceProjection.class)
                .orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public InstanceStatusResult getInstanceStatus() {
        InstanceStatusProjection instance = instanceRepository
                .findFirstByOrderByCreatedAtAsc(InstanceStatusProjection.class).orElse(null);
        if (instance == null || !instance.isSetupDone()) {
            return new InstanceStatusResult(false, "UNKNOWN", Instant.now());
        }
        return new InstanceStatusResult(instance.isSetupDone(), instance.getEdition(),
                instance.getUpdatedAt());
    }

    @Override
    @Transactional(readOnly = true)
    public ConfigResult getConfigurationValues() {
        Map<InstanceConfigurationKey, String> config =
                iConfigurationRepository.findByKeyIn(MANAGED_KEYS).stream().collect(Collectors
                        .toMap(InstanceConfiguration::getKey, InstanceConfiguration::getValue));

        ConfigResult result = ConfigResult.builder().configKeys(config)
                .adminBaseUrl(appProperties.getBaseUrls().getAdmin())
                .appBaseUrl(appProperties.getBaseUrls().getApp()).build();

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstanceConfigurationResponse> getAllInstanceConfigurations() {
        List<InstanceConfiguration> rows = iConfigurationRepository.findAll();

        return rows.stream()
                .map(ic -> InstanceConfigurationResponse.builder().id(ic.getId())
                        .createdAt(ic.getCreatedAt()).updatedAt(ic.getUpdatedAt()).key(ic.getKey())
                        .value(cHelper.decryptIfNeeded(ic)).build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getConfigVersion() {
        Instance instance = instanceRepository.findFirstByOrderByCreatedAtAsc(Instance.class)
                .orElseThrow(() -> new AuthException(AuthErrorCodes.INSTANCE_NOT_CONFIGURED));

        return instance.getConfigVersion();
    }

    @Override
    @Transactional
    public Map<InstanceConfigurationKey, String> updateConfigurations(
            InstanceConfigurationUpdateRequest request) {
        InstanceConfigVersionProjection instance = instanceRepository
                .findFirstByOrderByCreatedAtAsc(InstanceConfigVersionProjection.class)
                .orElseThrow(() -> new AuthException(AuthErrorCodes.INSTANCE_NOT_CONFIGURED));

        Map<InstanceConfigurationKey, String> updates = toUpdateMap(request);
        List<InstanceConfiguration> iConfigs =
                iConfigurationRepository.findByKeyIn(updates.keySet());

        boolean bumpNeeded = false;
        for (InstanceConfiguration iConfig : iConfigs) {
            InstanceConfigurationKey key = iConfig.getKey();
            if (VERSIONED_KEYS.contains(key)) {
                bumpNeeded = true;
            }
            cHelper.encryptIfNeeded(iConfig, updates.get(key));
        }

        if (bumpNeeded) {
            Long expected = instance.getConfigVersion();
            if (expected != null) {
                int bumped = instanceRepository.bumpConfigVersion(instance.getId(), expected,
                        Instant.now());
                if (bumped != 1) {
                    throw new ConcurrentModificationException("Config modified concurrently");
                }
            }
        }

        return iConfigurationRepository.saveAll(iConfigs).stream().collect(
                Collectors.toMap(InstanceConfiguration::getKey, InstanceConfiguration::getValue));
    }

    @Override
    @Transactional
    public AuthAdminResult adminSignup(AdminSignupRequest payload) {
        // check if instance exist;
        Instance instance =
                instanceRepository.findFirstByOrderByCreatedAtAsc(Instance.class).orElse(null);
        if (instance == null) {
            throw new AuthException(AuthErrorCodes.INSTANCE_NOT_CONFIGURED);
        }

        // check if instance already has admin;
        UUID instanceAdminId =
                iAdminRepository.findFirstIdByOrderByCreatedAtAsc().map(OnlyId::getId).orElse(null);
        if (instanceAdminId != null) {
            throw new AuthException(AuthErrorCodes.ADMIN_ALREADY_EXIST);
        }

        // return error if the email and password is not present

        // validate the email or do it on user-service
        String email = normalizeEmail(payload.getEmail());

        // check if already a user exists or not
        UUID userId = userClient.getUserIdByEmail(email);
        if (userId != null) {
            // we don't send password back
            throw new AuthException(AuthErrorCodes.ADMIN_USER_ALREADY_EXIST,
                    Map.of("email", email, "firstName", payload.getFirstName(), "lastName",
                            payload.getLastName(), "companyName", payload.getCompanyName()));
        }

        // we create user in user-service and it sends a UserEvent
        // user-service is source of truth
        AuthAdminResult res = userClient.createUser(payload);
        instance.finishSetup(payload.getCompanyName());
        instance = instanceRepository.save(instance);
        InstanceAdmin instanceAdmin = InstanceAdmin.create(instance, res.getUserId());
        iAdminRepository.save(instanceAdmin);
        return res;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstanceAdminProjection> getInstanceAdmins() {
        UUID instance = instanceRepository.findFirstByOrderByCreatedAtAsc(OnlyId.class)
                .map(OnlyId::getId).orElse(null);
        if (instance == null) {
            throw new AuthException(AuthErrorCodes.INSTANCE_NOT_CONFIGURED);
        }
        List<InstanceAdminProjection> projections = iAdminRepository.findAllByInstanceId(instance);
        for (InstanceAdminProjection instanceAdminProjection : projections) {
            log.info("instance: {}", instanceAdminProjection.getUser());
        }
        return projections;
    }

    private Map<InstanceConfigurationKey, String> toUpdateMap(
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

    @Getter
    @Builder
    public static class ConfigResult {
        private Map<InstanceConfigurationKey, String> configKeys;
        private boolean isSmtpConfigured;
        private String adminBaseUrl;
        private String appBaseUrl;
    }
}
