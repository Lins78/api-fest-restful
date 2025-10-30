🍽️ API FEST RESTful

Uma API REST completa para sistema de gerenciamento de restaurante, desenvolvida com Spring Boot 3.2.0 e Java 21.

📋 Sobre o Projeto

Esta API fornece um sistema completo para gerenciamento de restaurantes, incluindo:
- Clientes: Cadastro e gerenciamento de clientes
- Produtos: Catálogo de produtos/pratos
- Restaurantes: Informações dos estabelecimentos
- Pedidos: Sistema de pedidos com relacionamentos

🚀 Tecnologias Utilizadas

- Java 21
- Spring Boot 3.2.0
- Spring Data JPA
- Maven 3.9.5
- H2 Database (desenvolvimento)
- PostgreSQL (produção)
- Jakarta Persistence

## Estrutura do Projeto


src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── exemplo/
│   │           └── apifest/
│   │               ├── ApiFestRestfullApplication.java  # Classe principal
│   │               ├── model/                           # Entidades JPA
│   │               │   ├── Cliente.java
│   │               │   ├── Pedido.java
│   │               │   ├── Produto.java
│   │               │   └── Restaurante.java
│   │               └── repository/                      # Interfaces de Repository
│   │                   ├── ClienteRepository.java
│   │                   ├── PedidoRepository.java
│   │                   ├── ProdutoRepository.java
│   │                   └── RestauranteRepository.java
│   └── resources/
│       └── application.properties                       # Configurações da aplicação
└── pom.xml                                             # Dependências Maven


Entidades

Cliente
- ID, nome, email, telefone, endereço
- Data de cadastro e status ativo/inativo
- Validação de email único

Produto
- ID, nome, descrição, preço
- Status ativo/inativo

Pedido
- ID, descrição, valor, data do pedido
- Relacionamento com Cliente (Many-to-One)
- Status ativo/inativo

Restaurante
- ID, nome, endereço, telefone
- Status ativo/inativo

Repositórios

Cada entidade possui um repository com métodos básicos do JPA e consultas customizadas:
- Busca por registros ativos (`findByAtivoTrue()`)
- Busca por nome (case-insensitive, `findByNomeContainingIgnoreCase()`)
- Validações específicas (ex: `existsByEmail()` para Cliente)

Tecnologias Utilizadas

- Spring Boot 3.2.0 - Framework principal
- Spring Data JPA - Persistência de dados
- H2 Database - Banco de dados em memória (desenvolvimento)
- Lombok - Redução de código boilerplate
- Maven - Gerenciamento de dependências

Como Executar

🔧 Desenvolvimento (H2 - Padrão)
1. Certifique-se de ter o Java 17+ instalado
2. Execute na raiz do projeto:
   
   mvnw.cmd spring-boot:run
   
3. A aplicação estará disponível em: `http://localhost:8080`
4. Console H2: `http://localhost:8080/h2-console`
   - URL: `jdbc:h2:mem:testdb`
   - Usuário: `sa`
   - Senha: `password`

🐘 Produção (PostgreSQL)

1. Instalar PostgreSQL
- Baixe e instale PostgreSQL: https://www.postgresql.org/download/
- Anote a senha do usuário `postgres`

2. Criar Banco de Dados
-- Conectar ao PostgreSQL e executar:
CREATE DATABASE api_fest_db;
CREATE USER api_user WITH PASSWORD 'senha123';
GRANT ALL PRIVILEGES ON DATABASE api_fest_db TO api_user;


3. Executar com Perfil de Produção

Usando perfil PostgreSQL
mvnw.cmd spring-boot:run -Dspring.profiles.active=prod


4. Configuração Personalizada (Opcional)
Ajuste as credenciais em `application.properties` na seção `prod`:

spring.datasource.url=jdbc:postgresql://localhost:5432/api_fest_db
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha


Banco de Dados

Criação Automática de Tabelas
**NÃO É NECESSÁRIO** criar tabelas manualmente! O Hibernate faz isso automaticamente:

- Desenvolvimento (H2): `spring.jpa.hibernate.ddl-auto=create-drop`
  - Tabelas são criadas na inicialização e removidas ao parar
- Produção (PostgreSQL): `spring.jpa.hibernate.ddl-auto=update`
  - Tabelas são criadas/alteradas conforme necessário

Tabelas Criadas Automaticamente:

-- Tabela de clientes
CREATE TABLE clientes (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255),
    email VARCHAR(255),
    telefone VARCHAR(255),
    endereco VARCHAR(255),
    data_cadastro TIMESTAMP,
    ativo BOOLEAN
);

-- Tabela de produtos
CREATE TABLE produtos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255),
    descricao VARCHAR(255),
    preco DECIMAL,
    ativo BOOLEAN
);

-- Tabela de restaurantes
CREATE TABLE restaurantes (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255),
    endereco VARCHAR(255),
    telefone VARCHAR(255),
    ativo BOOLEAN
);

-- Tabela de pedidos (com relacionamento)
CREATE TABLE pedidos (
    id BIGSERIAL PRIMARY KEY,
    descricao VARCHAR(255),
    valor DECIMAL,
    data_pedido TIMESTAMP,
    cliente_id BIGINT REFERENCES clientes(id),
    ativo BOOLEAN
);


