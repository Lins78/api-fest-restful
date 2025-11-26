#!/bin/bash

# ===============================================================================
# SCRIPT DE VALIDAÇÃO - ROTEIRO 10 - API FEST RESTful
# ===============================================================================
# 
# Este script valida todas as implementações do Roteiro 10:
# - Cache (local e Redis)
# - Containerização (Docker + Compose)
# - Performance e testes
# 
# @author DeliveryTech Development Team
# @version 1.0 - Roteiro 10
# ===============================================================================

set -e

echo "🚀 VALIDAÇÃO ROTEIRO 10 - API FEST RESTful"
echo "=" | tr -d '\n' | xargs printf '%.0s' {1..50}; echo

# ========== VARIÁVEIS ==========
PROJECT_NAME="api-fest"
VERSION="1.0.0"
COMPOSE_FILE="docker-compose-roteiro10.yml"

# ========== FUNÇÕES AUXILIARES ==========

log_step() {
    echo -e "\n🔹 $1"
}

log_success() {
    echo -e "✅ $1"
}

log_error() {
    echo -e "❌ $1"
    exit 1
}

check_prerequisites() {
    log_step "Verificando pré-requisitos..."
    
    # Verificar Docker
    if ! command -v docker &> /dev/null; then
        log_error "Docker não está instalado!"
    fi
    log_success "Docker encontrado: $(docker --version)"
    
    # Verificar Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose não está instalado!"
    fi
    log_success "Docker Compose encontrado: $(docker-compose --version)"
    
    # Verificar Maven
    if [ -f "./mvnw" ]; then
        chmod +x mvnw
        log_success "Maven Wrapper encontrado"
    else
        log_error "Maven Wrapper não encontrado!"
    fi
}

test_compilation() {
    log_step "Testando compilação..."
    
    if ./mvnw clean compile -DskipTests -q; then
        log_success "Compilação bem-sucedida"
    else
        log_error "Falha na compilação"
    fi
}

test_cache_functionality() {
    log_step "Testando funcionalidade de cache..."
    
    if ./mvnw test -Dtest="CachePerformanceTest" -q; then
        log_success "Testes de cache executados com sucesso"
    else
        log_error "Falha nos testes de cache"
    fi
}

build_docker_image() {
    log_step "Construindo imagem Docker..."
    
    # Build da imagem
    if docker build -t ${PROJECT_NAME}:${VERSION} .; then
        log_success "Imagem Docker construída: ${PROJECT_NAME}:${VERSION}"
    else
        log_error "Falha na construção da imagem Docker"
    fi
    
    # Verificar tamanho da imagem
    IMAGE_SIZE=$(docker images ${PROJECT_NAME}:${VERSION} --format "table {{.Size}}" | tail -n +2)
    log_success "Tamanho da imagem: $IMAGE_SIZE"
}

test_docker_compose() {
    log_step "Testando Docker Compose..."
    
    # Copiar arquivo de exemplo de environment
    if [ ! -f ".env" ]; then
        cp .env.example .env
        log_success "Arquivo .env criado a partir do exemplo"
    fi
    
    # Validar arquivo compose
    if docker-compose -f $COMPOSE_FILE config > /dev/null 2>&1; then
        log_success "Arquivo Docker Compose válido"
    else
        log_error "Arquivo Docker Compose inválido"
    fi
    
    # Testar build via compose
    if docker-compose -f $COMPOSE_FILE build api-fest; then
        log_success "Build via Docker Compose bem-sucedido"
    else
        log_error "Falha no build via Docker Compose"
    fi
}

test_container_health() {
    log_step "Testando saúde dos containers..."
    
    # Subir apenas os serviços essenciais
    docker-compose -f $COMPOSE_FILE up -d postgres redis
    
    # Aguardar inicialização
    sleep 30
    
    # Verificar PostgreSQL
    if docker-compose -f $COMPOSE_FILE exec -T postgres pg_isready -U api_fest_user -d api_fest_db; then
        log_success "PostgreSQL está funcionando"
    else
        log_error "PostgreSQL não está funcionando"
    fi
    
    # Verificar Redis
    if docker-compose -f $COMPOSE_FILE exec -T redis redis-cli ping; then
        log_success "Redis está funcionando"
    else
        log_error "Redis não está funcionando"
    fi
    
    # Limpar containers
    docker-compose -f $COMPOSE_FILE down
}

run_performance_tests() {
    log_step "Executando testes de performance..."
    
    # Executar testes específicos de cache
    echo "📊 Executando testes de performance de cache..."
    if ./mvnw test -Dtest="CachePerformanceTest" -q; then
        log_success "Testes de performance executados"
    else
        log_error "Falha nos testes de performance"
    fi
}

generate_reports() {
    log_step "Gerando relatórios..."
    
    # Informações da imagem Docker
    echo "📋 RELATÓRIO DE VALIDAÇÃO - ROTEIRO 10" > validation-report.txt
    echo "=====================================" >> validation-report.txt
    echo "Data: $(date)" >> validation-report.txt
    echo "Projeto: API FEST RESTful" >> validation-report.txt
    echo "Versão: $VERSION" >> validation-report.txt
    echo "" >> validation-report.txt
    
    # Informações Docker
    echo "🐳 INFORMAÇÕES DOCKER:" >> validation-report.txt
    docker images ${PROJECT_NAME}:${VERSION} >> validation-report.txt
    echo "" >> validation-report.txt
    
    # Estrutura do projeto
    echo "📁 ESTRUTURA DO PROJETO:" >> validation-report.txt
    find . -name "*.java" -path "*/cache/*" -o -name "*Cache*" -o -name "Dockerfile*" -o -name "docker-compose*" | head -20 >> validation-report.txt
    echo "" >> validation-report.txt
    
    log_success "Relatório gerado: validation-report.txt"
}

cleanup() {
    log_step "Limpando recursos temporários..."
    
    # Parar containers se estiverem rodando
    docker-compose -f $COMPOSE_FILE down 2>/dev/null || true
    
    # Remover imagens antigas (opcional)
    # docker rmi ${PROJECT_NAME}:${VERSION} 2>/dev/null || true
    
    log_success "Limpeza concluída"
}

# ========== EXECUÇÃO PRINCIPAL ==========

main() {
    echo "🏁 Iniciando validação do Roteiro 10..."
    
    check_prerequisites
    test_compilation
    test_cache_functionality
    build_docker_image
    test_docker_compose
    test_container_health
    run_performance_tests
    generate_reports
    cleanup
    
    echo ""
    echo "🎉 VALIDAÇÃO CONCLUÍDA COM SUCESSO!"
    echo "✅ Cache implementado e testado"
    echo "✅ Containerização funcionando"
    echo "✅ Docker Compose configurado"
    echo "✅ Testes de performance executados"
    echo "✅ Relatório gerado"
    echo ""
    echo "📋 Próximos passos:"
    echo "  1. Execute: docker-compose -f $COMPOSE_FILE up -d"
    echo "  2. Acesse: http://localhost:8080/swagger-ui.html"
    echo "  3. Monitore: http://localhost:8080/actuator/health"
    echo ""
    echo "🎯 ROTEIRO 10 IMPLEMENTADO COM SUCESSO!"
}

# Executar apenas se o script for chamado diretamente
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi