package dev.twiceb.userservice.domain.model;

import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.fasterxml.jackson.databind.JsonNode;
import dev.twiceb.userservice.domain.model.support.JsonDefaults;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;

@Entity
@Table(name = "profiles")
@Getter
public class Profile extends AuditableEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "is_onboarded", nullable = false)
    private boolean onboarded;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "onboarding_step", columnDefinition = "jsonb", nullable = false)
    private JsonNode onboardingStep;

    @Column(name = "billing_address_country", length = 255, nullable = false)
    private String billingAddressCountry;

    @Getter(AccessLevel.NONE)
    @Column(name = "has_billing_address", nullable = false)
    private boolean hasBillingAddress;

    @Column(name = "last_tenant_id", nullable = true)
    private UUID lastTenantId;

    @Column(name = "company_name", length = 255, nullable = false)
    private String companyName;

    @Column(name = "role", length = 300)
    private String role;

    // Profile -> User (true 1:1; db enforces unique(user_id))
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = false)
    private User user;

    protected Profile() {} // jpa friendly

    public static Profile create(User user, String companyName) { // create a default Profile
        if (user == null) {
            throw new IllegalArgumentException("User is required");
        }
        Profile p = new Profile();
        p.id = UUID.randomUUID();
        p.onboarded = false;
        p.onboardingStep = JsonDefaults.profileOnboarding();
        p.billingAddressCountry = "UNITED STATES";
        p.hasBillingAddress = false;
        p.companyName = companyName != null ? companyName : "";
        p.role = "";
        p.user = user;

        return p;
    }

    public boolean getHasBillingAddress() {
        return hasBillingAddress;
    }

}
