# ========================================
# Script de Configuração Java 21 LTS
# API FEST RESTful - Setup Environment
# ========================================

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "API FEST RESTful - Java 21 LTS Setup" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan

# Configura JAVA_HOME para JDK 21
$jdk21Path = "C:\Program Files\Java\jdk-21"

if (Test-Path $jdk21Path) {
    $env:JAVA_HOME = $jdk21Path
    Write-Host "✅ JAVA_HOME configurado para: $env:JAVA_HOME" -ForegroundColor Green
    
    # Verifica a versão do Java
    Write-Host "`n🔍 Verificando versão do Java..." -ForegroundColor Yellow
    & java -version
    
    Write-Host "`n🔍 Verificando versão do Maven..." -ForegroundColor Yellow
    & .\mvnw.cmd -version
    
    Write-Host "`n✅ Ambiente configurado com sucesso para Java 21 LTS!" -ForegroundColor Green
    Write-Host "`n📋 Comandos disponíveis:" -ForegroundColor Cyan
    Write-Host "   • .\mvnw.cmd clean compile  - Compilar projeto" -ForegroundColor White
    Write-Host "   • .\mvnw.cmd test          - Executar testes" -ForegroundColor White
    Write-Host "   • .\mvnw.cmd spring-boot:run - Executar aplicação" -ForegroundColor White
    Write-Host "   • .\mvnw.cmd package       - Gerar JAR executável" -ForegroundColor White
    
} else {
    Write-Host "❌ ERRO: JDK 21 não encontrado em $jdk21Path" -ForegroundColor Red
    Write-Host "📋 JDKs disponíveis:" -ForegroundColor Yellow
    Get-ChildItem "C:\Program Files\Java" -Directory | ForEach-Object { 
        Write-Host "   • $($_.FullName)" -ForegroundColor White 
    }
}

Write-Host "`n========================================" -ForegroundColor Cyan