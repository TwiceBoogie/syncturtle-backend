package dev.twiceb.instanceservice.domain.model;

import java.time.Instant;
import java.util.UUID;
import dev.twiceb.instanceservice.domain.enums.InstanceEdition;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "instances")
public class Instance extends AuditableEntity {

    @Id
    private UUID id;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "edition", nullable = false)
    private InstanceEdition edition;

    // app binary
    @Column(name = "current_version", nullable = false)
    private String currentVersion;

    @Column(name = "latest_version")
    private String latestVersion;

    @Column(name = "last_checked_at", nullable = false)
    private Instant lastCheckedAt;

    @Column(name = "domain")
    private String domain;

    @Column(name = "namespace")
    private String namespace;

    @Column(name = "machine_signature", nullable = false, unique = true)
    private String machineSignature;

    @Column(name = "vm_host")
    private String vmHost;

    @Column(name = "is_setup_done", nullable = false)
    private boolean setupDone;

    @Column(name = "is_verified", nullable = false)
    private boolean verified;

    @Column(name = "is_test", nullable = false)
    private boolean test;

    @Column(name = "config_version", nullable = false)
    private long configVersion;

    @Column(name = "config_last_checked_at")
    private Instant configLastCheckedAt;

    protected Instance() {} // jpa friendly

    public void updateInstanceDetails(String currentVersion, String latestVersion, boolean isTest) {
        this.lastCheckedAt = Instant.now();
        this.currentVersion = currentVersion;
        this.latestVersion = latestVersion;
        this.test = isTest;
        this.edition = InstanceEdition.COMMUNITY;
    }

    public void finishSetup(String instanceName) {
        this.setupDone = true;
        this.name = instanceName;
        this.slug = makeSlug(this.name);
    }

    public static Instance register(String currentVersion, String latestVersion,
            String machineSignature, boolean isTest) {
        Instant now = Instant.now();
        Instance instance = new Instance();
        instance.id = UUID.randomUUID();
        instance.edition = InstanceEdition.COMMUNITY;
        instance.currentVersion = currentVersion;
        instance.latestVersion = latestVersion;
        instance.lastCheckedAt = now;
        instance.machineSignature = machineSignature;
        instance.test = isTest;
        instance.setupDone = false;
        instance.verified = false;
        instance.configVersion = 0L;
        instance.configLastCheckedAt = now;
        return instance;
    }

    private static String makeSlug(String name) {
        String base =
                name.trim().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return base + "-" + suffix;
    }
}
