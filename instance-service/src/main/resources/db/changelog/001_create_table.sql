CREATE TABLE IF NOT EXISTS instances (
    id UUID PRIMARY KEY,
    instance_name VARCHAR(255),
    instance_id VARCHAR(255) UNIQUE,
    current_version VARCHAR(50),
    edition VARCHAR(50),
    machine_signature VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by_id UUID
)