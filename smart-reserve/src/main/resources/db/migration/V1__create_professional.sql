CREATE TABLE professional (
            id UUID PRIMARY KEY,
            first_name VARCHAR(255) NOT NULL,
            last_name VARCHAR(255) NOT NULL,
            specialty VARCHAR(255) NOT NULL,
            active_status BOOLEAN NOT NULL,
            created_at TIMESTAMP(6) NOT NULL DEFAULT now(),
            updated_at TIMESTAMP(6));