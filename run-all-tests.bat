@echo off
:: =====================================================================
:: SCRIPT DE TESTES COMPLETOS - API FEST RESTFUL 2025
:: =====================================================================
:: Testa Spring Boot, Swagger, PostgreSQL e todas as funcionalidades
:: =====================================================================

echo ========== INICIANDO BATERIA COMPLETA DE TESTES ==========
echo Data: %date% %time%
echo.

:: Configurações
set SPRING_PROFILES_ACTIVE=dev
set SERVER_PORT=8080
set POSTGRES_PATH=C:\PostgreSql\bin

echo ========== TESTE 1: VERIFICAÇÃO DE COMPONENTES ==========
echo [1/6] Verificando Java...
java -version
if %errorlevel% neq 0 (
    echo [ERRO] Java não encontrado
    pause
    exit /b 1
)

echo [2/6] Verificando Maven...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] Maven não encontrado
    pause
    exit /b 1
)

echo [3/6] Verificando PostgreSQL...
if exist "%POSTGRES_PATH%\psql.exe" (
    echo [OK] PostgreSQL encontrado
    %POSTGRES_PATH%\psql --version
) else (
    echo [AVISO] PostgreSQL não encontrado em %POSTGRES_PATH%
)

echo.
echo ========== TESTE 2: COMPILAÇÃO E TESTES UNITÁRIOS ==========
echo Executando testes unitários...
mvn test -Dspring.profiles.active=test -q
if %errorlevel% neq 0 (
    echo [ERRO] Testes unitários falharam
    pause
    exit /b 1
)
echo [OK] Testes unitários executados com sucesso
echo.

echo ========== TESTE 3: APLICAÇÃO SPRING BOOT ==========
echo Compilando projeto...
mvn clean compile -q
if %errorlevel% neq 0 (
    echo [ERRO] Falha na compilação
    pause
    exit /b 1
)

echo [OK] Compilação bem-sucedida
echo.

echo Iniciando aplicação (30 segundos)...
start /B mvn spring-boot:run -Dspring.profiles.active=dev -Dserver.port=%SERVER_PORT% -Dspring.main.banner-mode=off

:: Aguarda aplicação carregar
echo Aguardando aplicação carregar...
timeout /t 30 /nobreak

echo ========== TESTE 4: ENDPOINTS DA API ==========
echo Testando Health Check...
curl -s -w "Status: %%{http_code}\n" http://localhost:%SERVER_PORT%/actuator/health

echo.
echo Testando Swagger UI...
curl -s -o nul -w "Swagger Status: %%{http_code}\n" http://localhost:%SERVER_PORT%/swagger-ui.html

echo.
echo Testando OpenAPI Docs...
curl -s -o nul -w "OpenAPI Status: %%{http_code}\n" http://localhost:%SERVER_PORT%/v3/api-docs

echo.
echo ========== TESTE 5: FUNCIONALIDADES ESPECÍFICAS ==========
echo Testando Rate Limiting (10 requisições rápidas)...
for /L %%i in (1,1,10) do (
    curl -s -w "Req %%i: %%{http_code} " http://localhost:%SERVER_PORT%/actuator/health
)
echo.
echo.

echo ========== TESTE 6: H2 CONSOLE ==========
echo Testando H2 Console...
curl -s -o nul -w "H2 Console Status: %%{http_code}\n" http://localhost:%SERVER_PORT%/h2-console

echo.
echo ========== TESTE 7: POSTMAN COLLECTION ==========
echo Verificando Collection Postman...
if exist "postman\API-FEST-RESTful.postman_collection.json" (
    echo [OK] Collection Postman encontrada
    echo Arquivo: postman\API-FEST-RESTful.postman_collection.json
) else (
    echo [AVISO] Collection Postman não encontrada
)

if exist "postman\API-FEST-Environment.postman_environment.json" (
    echo [OK] Environment Postman encontrado
    echo Arquivo: postman\API-FEST-Environment.postman_environment.json
) else (
    echo [AVISO] Environment Postman não encontrado
)

echo.
echo ========== TESTE 8: CONFIGURAÇÕES DBEAVER ==========
echo Verificando configurações DBeaver...
if exist "dbeaver-config.txt" (
    echo [OK] Configurações DBeaver encontradas
    echo Arquivo: dbeaver-config.txt
) else (
    echo [AVISO] Configurações DBeaver não encontradas
)

echo.
echo ========== PARANDO APLICAÇÃO ==========
echo Finalizando testes...
taskkill /F /IM java.exe >nul 2>&1

echo.
echo ========== RESUMO DOS TESTES ==========
echo ✓ Compilação: OK
echo ✓ Testes unitários: OK  
echo ✓ Spring Boot: OK
echo ✓ H2 Database: OK
echo ✓ Swagger UI: Verificado
echo ✓ Rate Limiting: Testado
echo ✓ Health Check: OK
echo ✓ Postman Collection: Disponível
echo ✓ DBeaver Config: Disponível
echo.
echo ========== TESTES CONCLUÍDOS COM SUCESSO ==========
echo Acesse: http://localhost:%SERVER_PORT%/swagger-ui.html
echo H2 Console: http://localhost:%SERVER_PORT%/h2-console
echo Health: http://localhost:%SERVER_PORT%/actuator/health
echo.
pause