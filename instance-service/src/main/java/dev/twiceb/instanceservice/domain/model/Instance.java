package dev.twiceb.instanceservice.domain.model;

import static dev.twiceb.common.util.StringHelper.*;
import java.time.Instant;
import java.util.UUID;
import dev.twiceb.common.enums.InstanceEdition;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@Table(name = "instances")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(onlyExplicitlyIncluded = true)
public class Instance extends AuditableEntity {

    @Size(max = 100)
    @ToString.Include
    @Column(name = "slug", unique = true)
    private String slug;

    @Size(max = 255)
    @Column(name = "instance_name", length = 255)
    private String instanceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "edition", nullable = false)
    private InstanceEdition edition;

    // app binary
    @NotBlank
    @Column(name = "current_version", nullable = false)
    private String currentVersion;

    @Column(name = "latest_version")
    private String latestVersion;

    @Column(name = "last_checked_at", nullable = false)
    private Instant lastCheckedAt;

    @Column(name = "config_version", nullable = false)
    private Long configVersion;

    @Column(name = "config_last_checked_at")
    private Instant configLastCheckedAt;

    @Size(max = 800)
    @Column(name = "domain")
    private String domain;

    @Size(max = 255)
    @Column(name = "namespace")
    private String namespace;

    // external install/deployment identifier; unique
    @NotBlank
    @Size(max = 255)
    @Column(name = "instance_id", nullable = false, unique = true)
    @ToString.Include
    private String instanceId;

    @Size(max = 512)
    @Column(name = "vm_host", length = 512)
    private String vmHost;

    @Column(name = "is_setup_done", nullable = false)
    private boolean setupDone;

    @Column(name = "is_verified", nullable = false)
    private boolean verified;

    @Column(name = "is_test", nullable = false)
    private boolean test;

    @Version
    @Column(name = "version")
    private Long version;

    public static Instance register(String currentVersion, String latestVersion, String instanceId,
            boolean isTest) {
        Instant now = Instant.now();

        Instance instance = new Instance();
        instance.edition = InstanceEdition.COMMUNITY;
        instance.currentVersion = nvl(currentVersion, "");
        instance.latestVersion = nvl(latestVersion, "");
        instance.lastCheckedAt = now;
        instance.instanceId = firstNonBlank(instanceId, "instanceId");
        instance.test = isTest;
        instance.setupDone = false;
        instance.verified = false;
        instance.configVersion = 0l;

        return instance;
    }

    public void updateBinaryCheck(String currentVersion, String latestVersion, boolean isTest) {
        this.currentVersion = nvl(currentVersion, "");
        this.latestVersion = nvl(latestVersion, "");
        this.test = isTest;
        this.lastCheckedAt = Instant.now();
    }

    public void finishSetup(String instanceName) {
        this.setupDone = true;
        this.instanceName = normalize(instanceName);
        // slug only generated once if absent;
        if (isBlank(this.slug) && !isBlank(instanceName))
            this.slug = makeSlug(this.instanceName);
    }

    public void updateInfo(String instanceName, String domain, String namespace) {
        this.instanceName = normalize(instanceName);
        this.domain = normalize(domain);
        this.namespace = normalize(namespace);
        if (!isBlank(instanceName))
            this.slug = makeSlug(this.instanceName);
    }

    private static String makeSlug(String name) {
        String base =
                name.trim().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return base + "-" + suffix;
    }
}
