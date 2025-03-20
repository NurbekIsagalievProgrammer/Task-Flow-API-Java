CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO users (email, first_name, last_name, password, role)
VALUES (
    'admin@example.com',
    'Admin',
    'User',
    crypt('admin123', gen_salt('bf')),
    'ADMIN'
) ON CONFLICT (email) DO NOTHING; 