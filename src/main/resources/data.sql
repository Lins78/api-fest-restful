-- ==========================================================================
-- ROTEIRO 7 - DADOS DE TESTE PARA AUTENTICAÇÃO E AUTORIZAÇÃO
-- ==========================================================================
-- Este arquivo contém usuários de teste para validação do sistema de segurança
-- implementado no Roteiro 7. Inclui usuários com diferentes perfis de acesso.
-- 
-- SENHAS: Todas as senhas são "123456" (hash BCrypt)
-- Hash gerado: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXulpZR8J4OY6Nd4EMCFyZw4ufC
-- ==========================================================================

-- Inserir usuários de teste com diferentes roles
INSERT INTO usuarios (id, nome, email, senha, telefone, role, ativo, data_criacao, restaurante_id) VALUES
(1, 'Admin Sistema', 'admin@delivery.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXulpZR8J4OY6Nd4EMCFyZw4ufC', '(11) 99999-0001', 'ADMIN', true, NOW(), null),
(2, 'João Cliente', 'joao@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXulpZR8J4OY6Nd4EMCFyZw4ufC', '(11) 99999-0002', 'CLIENTE', true, NOW(), null),
(3, 'Pizza Palace', 'pizza@palace.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXulpZR8J4OY6Nd4EMCFyZw4ufC', '(11) 99999-0003', 'RESTAURANTE', true, NOW(), 1),
(4, 'Carlos Entregador', 'carlos@entrega.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXulpZR8J4OY6Nd4EMCFyZw4ufC', '(11) 99999-0004', 'ENTREGADOR', true, NOW(), null),
(5, 'Maria Cliente', 'maria@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXulpZR8J4OY6Nd4EMCFyZw4ufC', '(11) 99999-0005', 'CLIENTE', true, NOW(), null),
(6, 'Burger House', 'burger@house.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXulpZR8J4OY6Nd4EMCFyZw4ufC', '(11) 99999-0006', 'RESTAURANTE', true, NOW(), 2);

-- ==========================================================================
-- INFORMAÇÕES IMPORTANTES:
-- 
-- 1. CREDENCIAIS DE TESTE:
--    - admin@delivery.com / 123456 (ADMIN)
--    - joao@email.com / 123456 (CLIENTE) 
--    - pizza@palace.com / 123456 (RESTAURANTE)
--    - carlos@entrega.com / 123456 (ENTREGADOR)
--    - maria@email.com / 123456 (CLIENTE)
--    - burger@house.com / 123456 (RESTAURANTE)
--
-- 2. PERFIS DE ACESSO:
--    - ADMIN: Acesso total ao sistema
--    - CLIENTE: Fazer pedidos e ver histórico
--    - RESTAURANTE: Gerenciar produtos e pedidos recebidos
--    - ENTREGADOR: Ver pedidos para entrega
--
-- 3. RELACIONAMENTOS:
--    - Pizza Palace (id=3) → Restaurante ID=1
--    - Burger House (id=6) → Restaurante ID=2
--
-- 4. PARA TESTAR:
--    POST /api/auth/login
--    {
--      "email": "admin@delivery.com",
--      "senha": "123456"
--    }
-- ==========================================================================