CREATE TYPE domain_status AS ENUM ('EXPIRED', 'SOON', 'ACTIVE', 'DELETED');
-- CREATE UNIQUE INDEX idx_unique_account_domain ON keychain (account_id, domain);
CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS encryption_key (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    vector BYTEA NOT NULL,
    dek VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);
ALTER TABLE encryption_key
ADD CONSTRAINT unique_vector_dek UNIQUE (vector, dek);
CREATE TABLE IF NOT EXISTS keychain (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    account_id BIGINT NOT NULL,
    encrypted_password BYTEA NOT NULL,
    domain VARCHAR(255) NOT NULL,
    status VARCHAR(255) DEFAULT 'ACTIVE',
    update_date DATE NOT NULL,
    encryption_key_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (account_id) REFERENCES accounts(id),
    FOREIGN KEY (encryption_key_id) REFERENCES encryption_key(id)
);
ALTER TABLE keychain
ADD CONSTRAINT unique_account_domain UNIQUE (account_id, domain);