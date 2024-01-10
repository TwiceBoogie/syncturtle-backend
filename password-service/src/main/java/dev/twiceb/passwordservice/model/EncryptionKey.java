package dev.twiceb.passwordservice.model;

import dev.twiceb.passwordservice.service.util.TransitConverter;
import jakarta.persistence.*;
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
    @Convert(converter = TransitConverter.class)
    private String dek;

    @Column(name = "vector", columnDefinition = "bytea")
    private byte[] vector;

    @OneToOne(mappedBy = "encryptionKey", cascade = CascadeType.ALL, orphanRemoval = true)
    private Keychain keychain;

    public EncryptionKey() {}

    public EncryptionKey(String dek, byte[] vector) {
        this.dek = dek;
        this.vector = vector;
    }
}
