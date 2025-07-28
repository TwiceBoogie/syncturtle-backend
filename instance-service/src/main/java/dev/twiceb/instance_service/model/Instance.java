package dev.twiceb.instance_service.model;

import java.time.Instant;
import java.util.UUID;
import dev.twiceb.instance_service.enums.InstanceEdition;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "instances")
public class Instance extends AuditableEntity {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(name = "slug", unique = true, nullable = false)
    private String slug;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "edition", nullable = false)
    private InstanceEdition edition;

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
    private boolean setupDone = false;

    @Column(name = "is_verified", nullable = false)
    private boolean verified = false;

    @Column(name = "is_test", nullable = false)
    private boolean test = false;
}
