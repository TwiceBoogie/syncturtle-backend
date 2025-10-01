package dev.twiceb.instanceservice.domain.model;

import java.math.BigDecimal;
import java.util.UUID;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import dev.twiceb.instanceservice.domain.model.support.PlanFeatures;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;

@Getter
@Entity
@Table(name = "plans")
public class Plan {

    @Id
    private UUID id;

    @NaturalId(mutable = false)
    @Column(name = "key", length = 50, nullable = false)
    private String key;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Type(JsonType.class)
    @Column(name = "features", columnDefinition = "jsonb", nullable = false)
    private PlanFeatures feature;

    @Column(name = "price_per_month", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerMonth;

    @Version
    private long version;

    protected Plan() {} // jpa friendly
}
