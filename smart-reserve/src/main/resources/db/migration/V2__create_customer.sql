CREATE TABLE customer (
                          id UUID PRIMARY KEY,
                          first_name VARCHAR(255) NOT NULL,
                          last_name VARCHAR(255) NOT NULL,
                          email VARCHAR(255) NOT NULL UNIQUE,
                          phone VARCHAR(50),
                          active_status BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at TIMESTAMP(6) NOT NULL DEFAULT now(),
                          updated_at TIMESTAMP(6)
);