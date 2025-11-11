-- ====================================================================
-- SETUP DATABASE API_FEST_DB
-- ====================================================================

-- 1. Conectar como usuário postgres
-- psql -U postgres

-- 2. Criar o banco de dados
CREATE DATABASE api_fest_db;

-- 3. Criar usuário específico
CREATE USER api_user WITH PASSWORD 'senha123';

-- 4. Conceder privilégios
GRANT ALL PRIVILEGES ON DATABASE api_fest_db TO api_user;

-- 5. Conectar ao novo banco
\c api_fest_db

-- 6. Conceder privilégios no schema public
GRANT ALL ON SCHEMA public TO api_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO api_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO api_user;

-- 7. Verificar conexão
SELECT version();
SELECT current_database();

-- 8. Sair
\q
