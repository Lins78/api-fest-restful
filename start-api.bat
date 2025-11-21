@echo off
echo =====================================
echo   API FEST RESTful - Spring Boot
echo =====================================
echo.
echo Iniciando aplicacao na porta 8080...
echo.
echo URLs disponiveis:
echo - Swagger UI: http://localhost:8080/swagger-ui.html
echo - H2 Console: http://localhost:8080/h2-console
echo - Home API: http://localhost:8080/api/home
echo.
echo Pressione Ctrl+C para parar a aplicacao
echo.

java -jar -Dspring.profiles.active=dev target/api-fest-restfull-1.0.0.jar

echo.
echo Aplicacao finalizada.
pause