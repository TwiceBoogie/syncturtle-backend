CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');

-- DO $$
-- BEGIN
--     IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role') THEN
--         CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');
--     END IF;
-- END $$;


-- user_status can be (Suspended, inactive, active, Archived, blocked, expired, pending_deletion, locked)
-- serial_number UUID DEFAULT gen_random_uuid(),
CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(36) NOT NULL,
    last_name VARCHAR(36) NOT NULL,
    password VARCHAR(255),
    verified BOOLEAN DEFAULT FALSE,
    role user_role DEFAULT 'USER' NOT NULL,
    user_status VARCHAR(36),
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