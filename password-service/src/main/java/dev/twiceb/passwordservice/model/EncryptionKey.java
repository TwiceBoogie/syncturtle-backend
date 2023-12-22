package dev.twiceb.passwordsservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "encryption_key", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "dek", "vector" })
})
public class EncryptionKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // dek = data encryption key
    @Column(name = "dek")
    private String dek;

    @Column(name = "vector", columnDefinition = "bytea")
    private byte[] vector;
}
