@echo off
echo ===========================================
echo ROTEIRO 9 - FASE 4: TESTE DE COBERTURA JACOCO
echo ===========================================
echo.

echo [1] Limpando projeto...
call mvn clean

echo.
echo [2] Executando testes de validacao com cobertura...
call mvn test -Dtest=ValidatorTest

echo.
echo [3] Gerando relatorio de cobertura...
call mvn jacoco:report

echo.
echo [4] Verificando quality gates...
call mvn jacoco:check

echo.
echo [RESULTADO] Relatorio gerado em: target\site\jacoco\index.html
echo [CONFIGURACAO] Quality Gates: 80%% instrucoes, 70%% branches, 75%% metodos, 80%% classes
echo.

pause