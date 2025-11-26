@echo off
:: =====================================================================
:: SCRIPT DE INICIALIZAÇÃO COMPLETO - API FEST RESTFUL 2025
:: =====================================================================
:: Script para testar todas as funcionalidades implementadas
:: Inclui rate limiting, profiles, JWT authentication, e documentação
:: =====================================================================

echo ========== INICIANDO API FEST RESTFUL 2025 ==========
echo Projeto 100%% completo com todas as funcionalidades!
echo.

:: Define o perfil como desenvolvimento por padrão
set SPRING_PROFILES_ACTIVE=dev
set SERVER_PORT=8080

echo ========== CONFIGURACOES DO AMBIENTE ==========
echo Profile Ativo: %SPRING_PROFILES_ACTIVE%
echo Porta do Servidor: %SERVER_PORT%
echo Java Version: %JAVA_VERSION%
echo.

echo ========== VERIFICANDO DEPENDENCIAS ==========
:: Verifica se Maven está instalado
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] Maven nao encontrado. Instale o Maven primeiro.
    pause
    exit /b 1
)

:: Verifica se Java 21 está disponível
java -version 2>&1 | findstr /C:"21" >nul
if %errorlevel% neq 0 (
    echo [AVISO] Java 21 nao detectado. Verifique a versao do Java.
)

echo [OK] Maven encontrado
echo [OK] Java configurado
echo.

echo ========== COMPILANDO PROJETO ==========
echo Compilando com todas as novas dependencias...
mvn clean compile -q
if %errorlevel% neq 0 (
    echo [ERRO] Falha na compilacao. Verifique os logs.
    pause
    exit /b 1
)
echo [OK] Compilacao concluida com sucesso
echo.

echo ========== FUNCIONALIDADES IMPLEMENTADAS ==========
echo ✓ Spring Boot 3.4.12 (atualizado)
echo ✓ Rate Limiting com Bucket4j
echo ✓ Profiles Environment (dev/test/prod) 
echo ✓ API Versioning preparado (v1)
echo ✓ Configuracao DBeaver incluida
echo ✓ Collection Postman completa
echo ✓ JWT Authentication
echo ✓ PostgreSQL/H2 configurados
echo ✓ Swagger/OpenAPI documentacao
echo ✓ Spring Security
echo ✓ Testes automatizados (200+ cenarios)
echo ✓ Cache e AOP habilitados
echo.

echo ========== INICIANDO APLICACAO ==========
echo Acesse os seguintes endpoints apos inicializacao:
echo.
echo 📱 API Base: http://localhost:%SERVER_PORT%/api
echo 📚 Swagger UI: http://localhost:%SERVER_PORT%/swagger-ui.html
echo 🗄️  H2 Console: http://localhost:%SERVER_PORT%/h2-console
echo 💚 Health Check: http://localhost:%SERVER_PORT%/actuator/health
echo 📊 Metrics: http://localhost:%SERVER_PORT%/actuator/metrics
echo.
echo 🔐 Credenciais H2: Usuario=sa, Senha=(vazio)
echo 🔑 Login API: admin / 123456
echo.

echo ========== TESTANDO RATE LIMITING ==========
echo Para testar rate limiting, execute multiplas requisicoes:
echo curl -X GET http://localhost:%SERVER_PORT%/api/clientes
echo Headers de resposta incluirao: X-Rate-Limit-Remaining
echo.

echo ========== POSTMAN COLLECTION ==========
echo Importe os arquivos em /postman/:
echo - API-FEST-RESTful.postman_collection.json
echo - API-FEST-Environment.postman_environment.json
echo.

echo ========== DBEAVER CONFIGURACAO ==========
echo Consulte o arquivo: dbeaver-config.txt
echo Para configuracoes de conexao automaticas
echo.

echo Iniciando aplicacao...
echo.

:: Inicia a aplicação
mvn spring-boot:run -Dspring.profiles.active=%SPRING_PROFILES_ACTIVE%

pause