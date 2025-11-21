@echo off
echo ================================================
echo    TESTE ROTEIRO 8 - INTEGRACAO E PRODUCAO
echo ================================================

echo.
echo 1. Compilacao dos testes...
call mvn compile test-compile
if %ERRORLEVEL% neq 0 (
    echo ERRO: Falha na compilacao
    pause
    exit /b 1
)

echo.
echo 2. Executando testes de repositorio...
call mvn test -Dtest=ClienteRepositoryTest
if %ERRORLEVEL% neq 0 echo FALHA nos testes de repositorio

echo.
echo 3. Executando health checks...
call mvn test -Dtest=ActuatorIT
if %ERRORLEVEL% neq 0 echo FALHA nos health checks

echo.
echo 4. Iniciando aplicacao para teste manual...
echo Pressione Ctrl+C para parar a aplicacao
start cmd /c "mvn spring-boot:run"

echo.
echo 5. Aguardando aplicacao iniciar (30s)...
timeout /t 30 /nobreak > nul

echo.
echo 6. Testando endpoints Actuator...
echo.
echo - Health Check:
curl -s http://localhost:8080/actuator/health | findstr "UP" && echo   [OK] Health Check || echo   [ERRO] Health Check

echo.
echo - Info:
curl -s http://localhost:8080/actuator/info | findstr "api-fest" && echo   [OK] Info || echo   [ERRO] Info

echo.
echo - Metrics:
curl -s http://localhost:8080/actuator/metrics | findstr "names" && echo   [OK] Metrics || echo   [ERRO] Metrics

echo.
echo 7. Testando Swagger UI...
echo Abrindo Swagger UI no navegador...
start http://localhost:8080/swagger-ui/index.html

echo.
echo ================================================
echo    TESTE MANUAL CONCLUIDO
echo ================================================
echo.
echo Para parar a aplicacao:
echo 1. Va para a janela do Maven
echo 2. Pressione Ctrl+C
echo.
pause