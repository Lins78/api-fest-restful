# ====================================================================
# SETUP POSTGRESQL PARA API FEST RESTFUL
# Data: 11/11/2025
# Objetivo: Configurar PostgreSQL e validar conexão
# ====================================================================

Write-Host "🐘 === SETUP POSTGRESQL - API FEST RESTFUL ===" -ForegroundColor Cyan
Write-Host ""

# Verificar se PostgreSQL está instalado
Write-Host "🔍 Verificando instalação do PostgreSQL..." -ForegroundColor Yellow

$pgPath = Get-Command psql -ErrorAction SilentlyContinue
if (-not $pgPath) {
    Write-Host "❌ PostgreSQL não encontrado no PATH!" -ForegroundColor Red
    Write-Host ""
    Write-Host "💡 INSTRUÇÕES PARA INSTALAR POSTGRESQL:" -ForegroundColor Yellow
    Write-Host "1. Baixe PostgreSQL em: https://www.postgresql.org/download/windows/" -ForegroundColor Cyan
    Write-Host "2. Execute o instalador" -ForegroundColor Cyan
    Write-Host "3. Durante a instalação, anote a senha do usuário 'postgres'" -ForegroundColor Cyan
    Write-Host "4. Adicione o PostgreSQL ao PATH do Windows" -ForegroundColor Cyan
    Write-Host "5. Reinicie o terminal e execute este script novamente" -ForegroundColor Cyan
    Write-Host ""
    
    # Verificar se existe em localização padrão
    $defaultPaths = @(
        "C:\Program Files\PostgreSQL\16\bin\psql.exe",
        "C:\Program Files\PostgreSQL\15\bin\psql.exe",
        "C:\Program Files\PostgreSQL\14\bin\psql.exe"
    )
    
    $foundPath = $null
    foreach ($path in $defaultPaths) {
        if (Test-Path $path) {
            $foundPath = $path
            break
        }
    }
    
    if ($foundPath) {
        Write-Host "🔍 PostgreSQL encontrado em: $foundPath" -ForegroundColor Green
        Write-Host "💡 Adicione este caminho ao PATH do Windows!" -ForegroundColor Yellow
        
        # Testar se conseguimos usar o PostgreSQL
        Write-Host ""
        Write-Host "🧪 Testando PostgreSQL..." -ForegroundColor Yellow
        $pgDir = Split-Path $foundPath
        
        Write-Host "📝 Execute estes comandos manualmente:" -ForegroundColor Yellow
        Write-Host "   cd `"$pgDir`"" -ForegroundColor Cyan
        Write-Host "   .\psql.exe -U postgres" -ForegroundColor Cyan
        Write-Host ""
    }
    
    # Continuar mesmo assim para mostrar os comandos SQL
} else {
    Write-Host "✅ PostgreSQL encontrado: $($pgPath.Source)" -ForegroundColor Green
}

Write-Host ""
Write-Host "📋 COMANDOS SQL PARA CRIAR O BANCO DE DADOS:" -ForegroundColor Yellow
Write-Host ""

$sqlCommands = @"
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
"@

Write-Host $sqlCommands -ForegroundColor Cyan

# Salvar comandos em arquivo
$sqlFile = "setup-postgresql.sql"
$sqlCommands | Out-File -FilePath $sqlFile -Encoding UTF8
Write-Host ""
Write-Host "📄 Comandos salvos em: $sqlFile" -ForegroundColor Green

Write-Host ""
Write-Host "🚀 PRÓXIMOS PASSOS:" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. Execute os comandos SQL acima no PostgreSQL" -ForegroundColor Cyan
Write-Host "2. Inicie a aplicação com perfil PostgreSQL:" -ForegroundColor Cyan
Write-Host "   `$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'" -ForegroundColor Gray
Write-Host "   .\mvnw.cmd spring-boot:run -Dspring.profiles.active=prod" -ForegroundColor Gray
Write-Host ""
Write-Host "3. Execute os testes CRUD:" -ForegroundColor Cyan
Write-Host "   .\test-postgresql-crud.ps1" -ForegroundColor Gray
Write-Host ""

# Verificar se a aplicação está rodando
Write-Host "🔍 Verificando se a aplicação está rodando..." -ForegroundColor Yellow

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/home" -TimeoutSec 5
    Write-Host "✅ Aplicação está rodando!" -ForegroundColor Green
    Write-Host "🎯 Pronto para executar testes PostgreSQL!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Execute agora: .\test-postgresql-crud.ps1" -ForegroundColor Cyan
} catch {
    Write-Host "⚠️ Aplicação não está rodando" -ForegroundColor Yellow
    Write-Host "💡 Execute primeiro:" -ForegroundColor Yellow
    Write-Host "   `$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'" -ForegroundColor Cyan
    Write-Host "   .\mvnw.cmd spring-boot:run -Dspring.profiles.active=prod" -ForegroundColor Cyan
}

Write-Host ""
Write-Host "📊 CONFIGURAÇÕES DO APPLICATION-PROD.PROPERTIES:" -ForegroundColor Yellow
Write-Host ""

# Ler e exibir configurações do PostgreSQL
try {
    $prodProps = Get-Content "src\main\resources\application-prod.properties" -ErrorAction Stop
    foreach ($line in $prodProps) {
        if ($line -match "^spring\.datasource" -or $line -match "^spring\.jpa") {
            Write-Host "   $line" -ForegroundColor Gray
        }
    }
} catch {
    Write-Host "⚠️ Não foi possível ler application-prod.properties" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "🎉 SETUP POSTGRESQL CONCLUÍDO!" -ForegroundColor Cyan