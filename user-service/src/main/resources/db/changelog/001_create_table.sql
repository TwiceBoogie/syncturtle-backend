CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(36) NOT NULL,
    last_name VARCHAR(36) NOT NULL,
    password VARCHAR(255),
    active BOOLEAN DEFAULT FALSE,
    role user_role DEFAULT 'USER',
    created_by VARCHAR(36),
    created_date TIMESTAMP NOT NULL,
    modified_by VARCHAR(36),
    modified_date TIMESTAMP
);

CREATE TABLE IF NOT EXISTS activation_codes (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    hashed_code VARCHAR(255) NOT NULL,
    expiration_time TIMESTAMP NOT NULL,
    user_id BIGINT,
    created_by VARCHAR(36),
    created_date TIMESTAMP NOT NULL,
    modified_by VARCHAR(36),
    modified_date TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);