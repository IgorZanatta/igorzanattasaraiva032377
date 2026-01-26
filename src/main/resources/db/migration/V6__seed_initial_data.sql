-- V6__seed_initial_data.sql

-- Usuário inicial de teste (dev)
-- email: test@example.com
-- senha: Password123

INSERT INTO app_user (id, name, email, password_hash, active, created_at, updated_at, last_login)
VALUES (
    gen_random_uuid(),
    'Usuário de Teste',
    'test@example.com',
    '$2a$10$n2tbowbSIQMPTUe/dPj1sOPiOrGso6PbkyWPFkEXI5//EJjnzKFle',
    true,
    NOW(),
    NOW(),
    NULL
);
