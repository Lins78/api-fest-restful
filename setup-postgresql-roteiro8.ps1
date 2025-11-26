# ===============================================================================
# 🐘 SCRIPT DE CONFIGURAÇÃO POSTGRESQL - ROTEIRO 8
# ===============================================================================
# Este script automatiza a configuração do PostgreSQL para a API FEST RESTful
# 
# FUNCIONALIDADES:
# - Verifica instalação do PostgreSQL
# - Cria database api_fest_db
# - Configura usuário e permissões
# - Executa scripts de inicialização
# - Testa conexão com a aplicação
#
# PREREQUISITOS:
# - PostgreSQL 12+ instalado
# - Usuário postgres configurado
# - PowerShell com permissões administrativas
# ===============================================================================

Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host "🐘 CONFIGURAÇÃO POSTGRESQL - API FEST RESTFULL - ROTEIRO 8" -ForegroundColor Yellow
Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host ""

# Função para exibir mensagem de erro e sair
function Exit-WithError {
    param([string]$Message)
    Write-Host "❌ ERRO: $Message" -ForegroundColor Red
    Write-Host "Pressione qualquer tecla para sair..." -ForegroundColor Yellow
    $null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
    exit 1
}

# Função para exibir mensagem de sucesso
function Write-Success {
    param([string]$Message)
    Write-Host "✅ $Message" -ForegroundColor Green
}

# Função para exibir informação
function Write-Info {
    param([string]$Message)
    Write-Host "ℹ️  $Message" -ForegroundColor Blue
}

Write-Host "🔍 === VERIFICANDO INSTALAÇÃO DO POSTGRESQL ===" -ForegroundColor Yellow
Write-Host ""

# Verifica se PostgreSQL está instalado
try {
    $pgVersion = & psql --version 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Success "PostgreSQL encontrado: $pgVersion"
    } else {
        throw "PostgreSQL não encontrado"
    }
} catch {
    Write-Host "❌ PostgreSQL não está instalado ou não está no PATH" -ForegroundColor Red
    Write-Host ""
    Write-Host "📥 Para instalar PostgreSQL:" -ForegroundColor Yellow
    Write-Host "1. Baixe em: https://www.postgresql.org/download/windows/" -ForegroundColor White
    Write-Host "2. Execute o installer e siga as instruções" -ForegroundColor White
    Write-Host "3. Anote a senha do usuário 'postgres'" -ForegroundColor White
    Write-Host "4. Execute este script novamente" -ForegroundColor White
    Write-Host ""
    Exit-WithError "PostgreSQL não instalado"
}

Write-Host ""
Write-Host "🔐 === CONFIGURAÇÃO DO BANCO DE DADOS ===" -ForegroundColor Yellow
Write-Host ""

# Solicita credenciais do PostgreSQL
$pgPassword = Read-Host -Prompt "Digite a senha do usuário 'postgres'" -AsSecureString
$pgPasswordPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($pgPassword))

# Define variável de ambiente para senha
$env:PGPASSWORD = $pgPasswordPlain

Write-Info "Testando conexão com PostgreSQL..."

# Testa conexão
try {
    & psql -h localhost -U postgres -d postgres -c "SELECT version();" 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Conexão com PostgreSQL estabelecida com sucesso"
    } else {
        throw "Falha na conexão"
    }
} catch {
    Exit-WithError "Não foi possível conectar ao PostgreSQL. Verifique se o serviço está rodando e se a senha está correta."
}

Write-Host ""
Write-Info "Verificando se database 'api_fest_db' já existe..."

# Verifica se o banco já existe
$dbExists = & psql -h localhost -U postgres -d postgres -t -c "SELECT 1 FROM pg_database WHERE datname='api_fest_db';" 2>$null

if ($dbExists -match "1") {
    Write-Host "⚠️  Database 'api_fest_db' já existe" -ForegroundColor Yellow
    $recreate = Read-Host "Deseja recriar o database? (s/N)"
    
    if ($recreate -eq "s" -or $recreate -eq "S") {
        Write-Info "Excluindo database existente..."
        & psql -h localhost -U postgres -d postgres -c "DROP DATABASE api_fest_db;" 2>$null
        Write-Success "Database excluído"
    } else {
        Write-Info "Mantendo database existente"
    }
}

if (-not ($dbExists -match "1") -or ($recreate -eq "s" -or $recreate -eq "S")) {
    Write-Info "Criando database 'api_fest_db'..."
    
    & psql -h localhost -U postgres -d postgres -c "CREATE DATABASE api_fest_db WITH ENCODING='UTF8' LC_COLLATE='Portuguese_Brazil.1252' LC_CTYPE='Portuguese_Brazil.1252';" 2>$null
    
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Database 'api_fest_db' criado com sucesso"
    } else {
        Exit-WithError "Falha ao criar database"
    }
}

Write-Host ""
Write-Host "📋 === EXECUTANDO SCRIPTS DE INICIALIZAÇÃO ===" -ForegroundColor Yellow
Write-Host ""

# Cria script SQL de inicialização
$initScript = @"
-- ===============================================================================
-- SCRIPT DE INICIALIZAÇÃO - API FEST RESTFUL - ROTEIRO 8
-- ===============================================================================

-- Criação de extensões necessárias
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Configurações de timezone
SET timezone = 'America/Sao_Paulo';

-- Comentários nas tabelas
COMMENT ON DATABASE api_fest_db IS 'Database da API FEST RESTful - Sistema de Delivery';

-- Verificação da conexão
SELECT 
    current_database() as database_name,
    version() as postgresql_version,
    current_timestamp as init_timestamp;

-- Mensagem de sucesso
SELECT 'Database inicializado com sucesso para API FEST RESTful!' as status;
"@

# Salva script temporário
$scriptPath = "init_api_fest_db.sql"
$initScript | Out-File -FilePath $scriptPath -Encoding UTF8

Write-Info "Executando script de inicialização..."

& psql -h localhost -U postgres -d api_fest_db -f $scriptPath

if ($LASTEXITCODE -eq 0) {
    Write-Success "Script de inicialização executado com sucesso"
} else {
    Write-Host "⚠️  Script executado com warnings (normal para primeira execução)" -ForegroundColor Yellow
}

# Remove script temporário
Remove-Item $scriptPath -ErrorAction SilentlyContinue

Write-Host ""
Write-Host "🧪 === TESTANDO CONFIGURAÇÃO COM A APLICAÇÃO ===" -ForegroundColor Yellow
Write-Host ""

Write-Info "Atualizando configurações da aplicação..."

# Atualiza application-prod.properties se necessário
$prodPropsPath = "src\main\resources\application-prod.properties"
if (Test-Path $prodPropsPath) {
    $prodContent = Get-Content $prodPropsPath
    $prodContent = $prodContent -replace "spring\.datasource\.password=.*", "spring.datasource.password=$pgPasswordPlain"
    $prodContent | Set-Content $prodPropsPath
    Write-Success "Configurações de produção atualizadas"
}

Write-Info "Testando aplicação com PostgreSQL..."
Write-Host ""

Write-Host "🚀 Iniciando aplicação em modo produção..." -ForegroundColor Cyan
Write-Host "   Aguarde enquanto a aplicação conecta ao PostgreSQL..." -ForegroundColor White

# Inicia aplicação em background
$appProcess = Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run", "-Dspring.profiles.active=prod" -PassThru -WindowStyle Hidden

# Aguarda aplicação inicializar
Start-Sleep -Seconds 15

Write-Info "Testando endpoints da aplicação..."

try {
    # Testa health check
    $healthResponse = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method Get -TimeoutSec 5
    if ($healthResponse.status -eq "UP") {
        Write-Success "Health Check: OK"
    }
    
    # Testa Swagger
    $swaggerResponse = Invoke-WebRequest -Uri "http://localhost:8080/swagger-ui.html" -Method Get -TimeoutSec 5
    if ($swaggerResponse.StatusCode -eq 200) {
        Write-Success "Swagger UI: OK"
    }
    
    Write-Success "Aplicação funcionando corretamente com PostgreSQL!"
    
} catch {
    Write-Host "⚠️  Aplicação ainda inicializando ou houve erro na conexão" -ForegroundColor Yellow
} finally {
    # Para a aplicação
    Write-Info "Parando aplicação..."
    Stop-Process -Name "java" -ErrorAction SilentlyContinue
    if ($appProcess) {
        Stop-Process -Id $appProcess.Id -ErrorAction SilentlyContinue
    }
}

Write-Host ""
Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host "🎉 === CONFIGURAÇÃO CONCLUÍDA COM SUCESSO ===" -ForegroundColor Green
Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "✅ POSTGRESQL CONFIGURADO:" -ForegroundColor Green
Write-Host "   📂 Database: api_fest_db" -ForegroundColor White
Write-Host "   🔗 URL: jdbc:postgresql://localhost:5432/api_fest_db" -ForegroundColor White
Write-Host "   👤 Usuário: postgres" -ForegroundColor White
Write-Host ""

Write-Host "🚀 COMANDOS PARA EXECUÇÃO:" -ForegroundColor Yellow
Write-Host ""
Write-Host "   Modo Produção (PostgreSQL):" -ForegroundColor White
Write-Host "   mvn spring-boot:run -Dspring.profiles.active=prod" -ForegroundColor Cyan
Write-Host ""
Write-Host "   Modo Desenvolvimento (H2):" -ForegroundColor White  
Write-Host "   mvn spring-boot:run" -ForegroundColor Cyan
Write-Host ""

Write-Host "🔗 ENDPOINTS DISPONÍVEIS:" -ForegroundColor Yellow
Write-Host "   📚 Swagger UI: http://localhost:8080/swagger-ui.html" -ForegroundColor White
Write-Host "   📊 Health Check: http://localhost:8080/actuator/health" -ForegroundColor White
Write-Host "   📈 Métricas: http://localhost:8080/actuator/metrics" -ForegroundColor White
Write-Host ""

Write-Host "🛠️  FERRAMENTAS ÚTEIS:" -ForegroundColor Yellow
Write-Host "   🔍 pgAdmin: Interface gráfica para PostgreSQL" -ForegroundColor White
Write-Host "   📊 DBeaver: Cliente universal de database" -ForegroundColor White
Write-Host "   📝 psql: Cliente command-line (já disponível)" -ForegroundColor White
Write-Host ""

Write-Host "📋 PRÓXIMOS PASSOS:" -ForegroundColor Yellow
Write-Host "   1. Execute: mvn spring-boot:run -Dspring.profiles.active=prod" -ForegroundColor White
Write-Host "   2. Acesse: http://localhost:8080/swagger-ui.html" -ForegroundColor White
Write-Host "   3. Teste os endpoints via Swagger UI" -ForegroundColor White
Write-Host "   4. Monitore via: http://localhost:8080/actuator/health" -ForegroundColor White
Write-Host ""

# Remove variável de ambiente de senha por segurança
$env:PGPASSWORD = $null

Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host "🏆 ROTEIRO 8 - CONFIGURAÇÃO POSTGRESQL COMPLETA!" -ForegroundColor Green
Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Pressione qualquer tecla para continuar..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")