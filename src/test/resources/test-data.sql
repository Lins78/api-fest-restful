-- ===============================================
-- DADOS DE TESTE PARA INTEGRAÇÃO - ROTEIRO 9
-- ===============================================
-- Scripts SQL para popular banco de testes com dados iniciais
-- Usado nos testes de integração com TestContainers
-- ===============================================

-- === DADOS DE TESTE PARA USUÁRIOS ===
INSERT INTO users (id, nome, email, password, ativo, created_at, updated_at) VALUES 
(1000, 'Admin Sistema', 'admin@sistema.com', '$2a$10$DummyHashForTestingPurposes', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1001, 'User Teste', 'user@teste.com', '$2a$10$DummyHashForTestingPurposes', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- === DADOS DE TESTE PARA CLIENTES ===
INSERT INTO clientes (id, nome, email, cpf, telefone, created_at, updated_at) VALUES 
(2000, 'Cliente Teste 1', 'cliente1@teste.com', '123.456.789-09', '(11) 99999-0001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2001, 'Cliente Teste 2', 'cliente2@teste.com', '987.654.321-00', '(11) 99999-0002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- === DADOS DE TESTE PARA ENDEREÇOS ===
INSERT INTO enderecos (id, cliente_id, cep, logradouro, numero, bairro, cidade, uf, created_at, updated_at) VALUES 
(3000, 2000, '01310-100', 'Av. Paulista', '1000', 'Bela Vista', 'São Paulo', 'SP', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3001, 2001, '04038-001', 'Rua Augusta', '500', 'Consolação', 'São Paulo', 'SP', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- === DADOS DE TESTE PARA CATEGORIAS ===
INSERT INTO categorias (id, nome, descricao, ativo, created_at, updated_at) VALUES 
(4000, 'Pizzas', 'Pizzas tradicionais e especiais', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4001, 'Bebidas', 'Refrigerantes, sucos e bebidas', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4002, 'Sobremesas', 'Doces e sobremesas', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- === DADOS DE TESTE PARA PRODUTOS ===
INSERT INTO produtos (id, nome, descricao, preco, categoria_id, estoque, ativo, created_at, updated_at) VALUES 
(5000, 'Pizza Margherita', 'Pizza com molho de tomate, mussarela e manjericão', 35.50, 4000, 50, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5001, 'Pizza Calabresa', 'Pizza com calabresa, cebola e mussarela', 38.90, 4000, 30, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5002, 'Refrigerante 2L', 'Coca-Cola 2 litros gelada', 8.00, 4001, 100, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5003, 'Pudim de Leite', 'Pudim caseiro de leite condensado', 12.50, 4002, 20, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- === DADOS DE TESTE PARA PEDIDOS ===
INSERT INTO pedidos (id, cliente_id, status, observacoes, valor_total, data_pedido, created_at, updated_at) VALUES 
(6000, 2000, 'AGUARDANDO_CONFIRMACAO', 'Pedido de teste 1', 44.40, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6001, 2001, 'CONFIRMADO', 'Pedido de teste 2', 51.40, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- === DADOS DE TESTE PARA ITENS DOS PEDIDOS ===
INSERT INTO itens_pedido (id, pedido_id, produto_id, quantidade, preco_unitario, subtotal, created_at, updated_at) VALUES 
-- Pedido 6000: Pizza Margherita (1x) + Refrigerante (1x) = 35.50 + 8.00 = 43.50 (diferença para testar cálculos)
(7000, 6000, 5000, 1, 35.50, 35.50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7001, 6000, 5002, 1, 8.00, 8.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Pedido 6001: Pizza Calabresa (1x) + Pudim (1x) = 38.90 + 12.50 = 51.40
(7002, 6001, 5001, 1, 38.90, 38.90, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7003, 6001, 5003, 1, 12.50, 12.50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- === ATUALIZAÇÃO DAS SEQUENCES ===
-- Ajustar sequences para evitar conflitos nos testes
ALTER SEQUENCE IF EXISTS users_id_seq RESTART WITH 2000;
ALTER SEQUENCE IF EXISTS clientes_id_seq RESTART WITH 3000;
ALTER SEQUENCE IF EXISTS enderecos_id_seq RESTART WITH 4000;
ALTER SEQUENCE IF EXISTS categorias_id_seq RESTART WITH 5000;
ALTER SEQUENCE IF EXISTS produtos_id_seq RESTART WITH 6000;
ALTER SEQUENCE IF EXISTS pedidos_id_seq RESTART WITH 7000;
ALTER SEQUENCE IF EXISTS itens_pedido_id_seq RESTART WITH 8000;