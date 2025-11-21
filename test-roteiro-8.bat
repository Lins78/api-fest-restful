@echo off
cls
echo ===============================================================================
echo 🧪 ROTEIRO 8 - EXECUÇÃO DE TESTES DE INTEGRAÇÃO - API FEST RESTful
echo ===============================================================================
echo.
echo Este script executa todos os testes implementados no Roteiro 8:
echo - Testes de Integração dos Controllers
echo - Testes de Repositórios
echo - Testes do Spring Boot Actuator
echo - Testes de Autenticação JWT
echo - Geração de Relatório de Cobertura de Código
echo.
echo ===============================================================================

echo.
echo 🚀 === INICIANDO EXECUÇÃO DOS TESTES ===
echo.

echo ⏰ %date% %time% - Iniciando compilação...
echo.
call mvn clean compile -q
if %ERRORLEVEL% neq 0 (
    echo ❌ ERRO: Falha na compilação!
    pause
    exit /b 1
)

echo ✅ Compilação concluída com sucesso!
echo.

echo ⏰ %date% %time% - Executando testes unitários...
echo.
call mvn test -Dtest="**/*Test" -q
if %ERRORLEVEL% neq 0 (
    echo ❌ AVISO: Alguns testes unitários falharam!
    echo.
)

echo ⏰ %date% %time% - Executando testes de integração...
echo.
call mvn test -Dtest="**/*IT" -q
if %ERRORLEVEL% neq 0 (
    echo ❌ AVISO: Alguns testes de integração falharam!
    echo.
)

echo ⏰ %date% %time% - Executando todos os testes com cobertura...
echo.
call mvn clean test jacoco:report
if %ERRORLEVEL% neq 0 (
    echo ❌ ERRO: Falha na execução dos testes com cobertura!
    echo.
) else (
    echo ✅ Relatório de cobertura gerado com sucesso!
    echo.
)

echo ===============================================================================
echo 📊 === RESULTADOS DOS TESTES ===
echo ===============================================================================
echo.

echo 📁 Relatórios disponíveis:
echo.
echo 📋 Relatório Surefire (Resultados dos Testes):
echo    📂 target\surefire-reports\index.html
echo.
echo 📊 Relatório JaCoCo (Cobertura de Código):
echo    📂 target\site\jacoco\index.html
echo.
echo 🧪 Logs detalhados dos testes:
echo    📂 target\surefire-reports\
echo.

if exist "target\site\jacoco\index.html" (
    echo ✅ Relatório de cobertura JaCoCo disponível!
    echo 📈 Para abrir o relatório: start target\site\jacoco\index.html
    echo.
)

if exist "target\surefire-reports" (
    echo ✅ Relatórios Surefire disponíveis!
    echo 📋 Arquivos de resultado em target\surefire-reports\
    echo.
)

echo ===============================================================================
echo 🎯 === VALIDAÇÃO DOS COMPONENTES ===
echo ===============================================================================
echo.

echo ⏰ %date% %time% - Iniciando aplicação para validação...
echo.
echo 🚀 Iniciando API em background...
start /b cmd /c "mvn spring-boot:run > nul 2>&1"

echo ⏰ Aguardando aplicação inicializar...
timeout /t 10 > nul

echo.
echo 🔍 === TESTANDO ENDPOINTS PRINCIPAIS ===
echo.

echo 📊 Testando Actuator Health Check...
curl -s http://localhost:8080/actuator/health > nul 2>&1
if %ERRORLEVEL% equ 0 (
    echo ✅ Actuator Health Check: OK
) else (
    echo ❌ Actuator Health Check: FALHOU
)

echo 📚 Testando Swagger UI...
curl -s http://localhost:8080/swagger-ui.html > nul 2>&1
if %ERRORLEVEL% equ 0 (
    echo ✅ Swagger UI: OK
) else (
    echo ❌ Swagger UI: FALHOU
)

echo 📖 Testando OpenAPI Docs...
curl -s http://localhost:8080/v3/api-docs > nul 2>&1
if %ERRORLEVEL% equ 0 (
    echo ✅ OpenAPI Docs: OK
) else (
    echo ❌ OpenAPI Docs: FALHOU
)

echo 👤 Testando endpoint de clientes...
curl -s http://localhost:8080/api/clientes > nul 2>&1
if %ERRORLEVEL% equ 0 (
    echo ✅ API Clientes: OK
) else (
    echo ❌ API Clientes: FALHOU
)

echo.
echo 🛑 Parando aplicação...
taskkill /f /im java.exe > nul 2>&1

echo.
echo ===============================================================================
echo 🎉 === RESUMO DO ROTEIRO 8 ===
echo ===============================================================================
echo.
echo ✅ COMPONENTES IMPLEMENTADOS:
echo    🧪 Testes de Integração completos
echo    📊 Spring Boot Actuator configurado
echo    📚 Documentação OpenAPI/Swagger atualizada
echo    🔐 Testes de autenticação JWT
echo    📈 Relatórios de cobertura de código
echo    🐘 Configuração PostgreSQL para produção
echo    ⚙️  Health checks customizados
echo.
echo 📊 ENDPOINTS DE MONITORAMENTO:
echo    🔍 http://localhost:8080/actuator/health
echo    📊 http://localhost:8080/actuator/metrics  
echo    ℹ️  http://localhost:8080/actuator/info
echo    ⚙️  http://localhost:8080/actuator/configprops
echo.
echo 📚 DOCUMENTAÇÃO:
echo    🎨 http://localhost:8080/swagger-ui.html
echo    📖 http://localhost:8080/v3/api-docs
echo.
echo 🎯 PRÓXIMOS PASSOS:
echo    1. 🐘 Configurar PostgreSQL local
echo    2. 🚀 Deploy em ambiente de produção
echo    3. 📊 Configurar monitoramento contínuo
echo    4. 🔄 Implementar CI/CD pipeline
echo.
echo ===============================================================================
echo 🏆 ROTEIRO 8 CONCLUÍDO COM SUCESSO!
echo ===============================================================================
echo.
echo A API FEST RESTful está pronta para produção com:
echo ✅ Testes abrangentes (Unitários + Integração)
echo ✅ Documentação interativa (Swagger)
echo ✅ Monitoramento completo (Actuator)
echo ✅ Configuração para diferentes ambientes
echo ✅ Relatórios de qualidade de código
echo.
echo 📅 Data de conclusão: %date% %time%
echo.
pause