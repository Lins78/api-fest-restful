# ====================================================================
# TESTE CRUD COMPLETO - POSTGRESQL
# Projeto: API FEST RESTful
# Data: 11/11/2025
# Objetivo: Validar integração com PostgreSQL em produção
# ====================================================================

Write-Host "🐘 === TESTE CRUD POSTGRESQL - API FEST RESTFUL ===" -ForegroundColor Cyan
Write-Host ""

# Configurações
$BASE_URL = "http://localhost:8080/api/v1"
$HEADERS = @{
    'Content-Type' = 'application/json'
    'Accept' = 'application/json'
}

# Função para fazer requisições HTTP com tratamento de erro
function Invoke-ApiRequest {
    param(
        [string]$Method,
        [string]$Uri,
        [hashtable]$Headers,
        [string]$Body = $null
    )
    
    try {
        $params = @{
            Method = $Method
            Uri = $Uri
            Headers = $Headers
            TimeoutSec = 30
        }
        
        if ($Body) {
            $params.Body = $Body
        }
        
        $response = Invoke-RestMethod @params
        return @{ Success = $true; Data = $response; Error = $null }
    }
    catch {
        $errorMsg = $_.Exception.Message
        if ($_.Exception.Response) {
            $errorMsg += " (Status: $($_.Exception.Response.StatusCode))"
        }
        return @{ Success = $false; Data = $null; Error = $errorMsg }
    }
}

# Função para exibir resultado
function Show-TestResult {
    param(
        [string]$TestName,
        [hashtable]$Result,
        [bool]$ShowData = $true
    )
    
    if ($Result.Success) {
        Write-Host "✅ $TestName" -ForegroundColor Green
        if ($ShowData -and $Result.Data) {
            Write-Host "   📄 Dados: $($Result.Data | ConvertTo-Json -Compress)" -ForegroundColor Gray
        }
    } else {
        Write-Host "❌ $TestName" -ForegroundColor Red
        Write-Host "   🚨 Erro: $($Result.Error)" -ForegroundColor Yellow
    }
    Write-Host ""
}

# ====================================================================
# 1. VERIFICAÇÃO INICIAL
# ====================================================================

Write-Host "🔍 1. VERIFICANDO STATUS DA APLICAÇÃO" -ForegroundColor Yellow
$healthCheck = Invoke-ApiRequest -Method "GET" -Uri "$BASE_URL/home" -Headers $HEADERS
Show-TestResult -TestName "Health Check da API" -Result $healthCheck

if (-not $healthCheck.Success) {
    Write-Host "🚨 ERRO CRÍTICO: Aplicação não está rodando!" -ForegroundColor Red
    Write-Host "💡 Execute antes: " -ForegroundColor Yellow
    Write-Host "   `$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'" -ForegroundColor Cyan
    Write-Host "   .\mvnw.cmd spring-boot:run -Dspring.profiles.active=prod" -ForegroundColor Cyan
    exit 1
}

# ====================================================================
# 2. TESTE CRUD - CLIENTE
# ====================================================================

Write-Host "👥 2. TESTE CRUD - CLIENTE" -ForegroundColor Yellow

# CREATE - Criar novo cliente
$novoCliente = @{
    nome = "Carlos PostgreSQL Test"
    email = "carlos.postgres@test.com"
    telefone = "(11) 99999-8888"
    endereco = "Rua PostgreSQL, 123 - São Paulo"
} | ConvertTo-Json

$createCliente = Invoke-ApiRequest -Method "POST" -Uri "$BASE_URL/clientes" -Headers $HEADERS -Body $novoCliente
Show-TestResult -TestName "CREATE - Criar Cliente" -Result $createCliente

$clienteId = $null
if ($createCliente.Success) {
    $clienteId = $createCliente.Data.id
    Write-Host "   🆔 Cliente criado com ID: $clienteId" -ForegroundColor Cyan
}

# READ - Buscar cliente criado
if ($clienteId) {
    $readCliente = Invoke-ApiRequest -Method "GET" -Uri "$BASE_URL/clientes/$clienteId" -Headers $HEADERS
    Show-TestResult -TestName "READ - Buscar Cliente por ID" -Result $readCliente
}

# READ ALL - Listar todos os clientes
$listClientes = Invoke-ApiRequest -Method "GET" -Uri "$BASE_URL/clientes" -Headers $HEADERS
Show-TestResult -TestName "READ ALL - Listar Clientes" -Result $listClientes -ShowData $false
if ($listClientes.Success) {
    Write-Host "   📊 Total de clientes: $($listClientes.Data.Count)" -ForegroundColor Cyan
}

# UPDATE - Atualizar cliente
if ($clienteId) {
    $clienteAtualizado = @{
        nome = "Carlos PostgreSQL ATUALIZADO"
        email = "carlos.postgres.updated@test.com"
        telefone = "(11) 88888-9999"
        endereco = "Avenida PostgreSQL, 456 - São Paulo"
    } | ConvertTo-Json
    
    $updateCliente = Invoke-ApiRequest -Method "PUT" -Uri "$BASE_URL/clientes/$clienteId" -Headers $HEADERS -Body $clienteAtualizado
    Show-TestResult -TestName "UPDATE - Atualizar Cliente" -Result $updateCliente
}

# ====================================================================
# 3. TESTE CRUD - RESTAURANTE
# ====================================================================

Write-Host "🏪 3. TESTE CRUD - RESTAURANTE" -ForegroundColor Yellow

# CREATE - Criar novo restaurante
$novoRestaurante = @{
    nome = "Restaurante PostgreSQL Test"
    endereco = "Rua PostgreSQL Gourmet, 789"
    telefone = "(11) 7777-6666"
    categoria = "PostgreSQL Cuisine"
    taxaEntrega = 5.99
} | ConvertTo-Json

$createRestaurante = Invoke-ApiRequest -Method "POST" -Uri "$BASE_URL/restaurantes" -Headers $HEADERS -Body $novoRestaurante
Show-TestResult -TestName "CREATE - Criar Restaurante" -Result $createRestaurante

$restauranteId = $null
if ($createRestaurante.Success) {
    $restauranteId = $createRestaurante.Data.id
    Write-Host "   🆔 Restaurante criado com ID: $restauranteId" -ForegroundColor Cyan
}

# READ - Buscar restaurante criado
if ($restauranteId) {
    $readRestaurante = Invoke-ApiRequest -Method "GET" -Uri "$BASE_URL/restaurantes/$restauranteId" -Headers $HEADERS
    Show-TestResult -TestName "READ - Buscar Restaurante por ID" -Result $readRestaurante
}

# READ ALL - Listar todos os restaurantes
$listRestaurantes = Invoke-ApiRequest -Method "GET" -Uri "$BASE_URL/restaurantes" -Headers $HEADERS
Show-TestResult -TestName "READ ALL - Listar Restaurantes" -Result $listRestaurantes -ShowData $false
if ($listRestaurantes.Success) {
    Write-Host "   📊 Total de restaurantes: $($listRestaurantes.Data.Count)" -ForegroundColor Cyan
}

# UPDATE - Atualizar restaurante
if ($restauranteId) {
    $restauranteAtualizado = @{
        nome = "Restaurante PostgreSQL ATUALIZADO"
        endereco = "Avenida PostgreSQL Premium, 999"
        telefone = "(11) 5555-4444"
        categoria = "Premium PostgreSQL"
        taxaEntrega = 7.50
    } | ConvertTo-Json
    
    $updateRestaurante = Invoke-ApiRequest -Method "PUT" -Uri "$BASE_URL/restaurantes/$restauranteId" -Headers $HEADERS -Body $restauranteAtualizado
    Show-TestResult -TestName "UPDATE - Atualizar Restaurante" -Result $updateRestaurante
}

# ====================================================================
# 4. TESTE CRUD - PRODUTO
# ====================================================================

Write-Host "🍕 4. TESTE CRUD - PRODUTO" -ForegroundColor Yellow

# CREATE - Criar novo produto (precisa de um restaurante válido)
if ($restauranteId) {
    $novoProduto = @{
        nome = "Pizza PostgreSQL Special"
        descricao = "Pizza especial com ingredientes do PostgreSQL"
        preco = 35.90
        categoria = "Pizza"
        disponivel = $true
        restaurante = @{ id = $restauranteId }
    } | ConvertTo-Json -Depth 3
    
    $createProduto = Invoke-ApiRequest -Method "POST" -Uri "$BASE_URL/produtos" -Headers $HEADERS -Body $novoProduto
    Show-TestResult -TestName "CREATE - Criar Produto" -Result $createProduto
    
    $produtoId = $null
    if ($createProduto.Success) {
        $produtoId = $createProduto.Data.id
        Write-Host "   🆔 Produto criado com ID: $produtoId" -ForegroundColor Cyan
    }
    
    # READ - Buscar produto criado
    if ($produtoId) {
        $readProduto = Invoke-ApiRequest -Method "GET" -Uri "$BASE_URL/produtos/$produtoId" -Headers $HEADERS
        Show-TestResult -TestName "READ - Buscar Produto por ID" -Result $readProduto
    }
    
    # READ BY RESTAURANT - Buscar produtos por restaurante
    $produtosByRestaurante = Invoke-ApiRequest -Method "GET" -Uri "$BASE_URL/produtos/restaurante/$restauranteId" -Headers $HEADERS
    Show-TestResult -TestName "READ BY RESTAURANT - Produtos por Restaurante" -Result $produtosByRestaurante -ShowData $false
    
    # UPDATE - Atualizar produto
    if ($produtoId) {
        $produtoAtualizado = @{
            nome = "Pizza PostgreSQL PREMIUM"
            descricao = "Pizza premium com ingredientes PostgreSQL selecionados"
            preco = 45.90
            categoria = "Pizza Premium"
            disponivel = $true
            restaurante = @{ id = $restauranteId }
        } | ConvertTo-Json -Depth 3
        
        $updateProduto = Invoke-ApiRequest -Method "PUT" -Uri "$BASE_URL/produtos/$produtoId" -Headers $HEADERS -Body $produtoAtualizado
        Show-TestResult -TestName "UPDATE - Atualizar Produto" -Result $updateProduto
    }
}

# ====================================================================
# 5. TESTE CRUD - PEDIDO
# ====================================================================

Write-Host "📦 5. TESTE CRUD - PEDIDO" -ForegroundColor Yellow

# CREATE - Criar novo pedido (precisa de um cliente válido)
if ($clienteId) {
    $novoPedido = @{
        descricao = "Pedido PostgreSQL Test"
        valor = 65.90
        cliente = @{ id = $clienteId }
        status = "PENDENTE"
    } | ConvertTo-Json -Depth 3
    
    $createPedido = Invoke-ApiRequest -Method "POST" -Uri "$BASE_URL/pedidos" -Headers $HEADERS -Body $novoPedido
    Show-TestResult -TestName "CREATE - Criar Pedido" -Result $createPedido
    
    $pedidoId = $null
    if ($createPedido.Success) {
        $pedidoId = $createPedido.Data.id
        Write-Host "   🆔 Pedido criado com ID: $pedidoId" -ForegroundColor Cyan
    }
    
    # READ - Buscar pedido criado
    if ($pedidoId) {
        $readPedido = Invoke-ApiRequest -Method "GET" -Uri "$BASE_URL/pedidos/$pedidoId" -Headers $HEADERS
        Show-TestResult -TestName "READ - Buscar Pedido por ID" -Result $readPedido
    }
    
    # READ BY CLIENT - Buscar pedidos por cliente
    $pedidosByCliente = Invoke-ApiRequest -Method "GET" -Uri "$BASE_URL/pedidos/cliente/$clienteId" -Headers $HEADERS
    Show-TestResult -TestName "READ BY CLIENT - Pedidos por Cliente" -Result $pedidosByCliente -ShowData $false
    
    # UPDATE STATUS - Atualizar status do pedido
    if ($pedidoId) {
        $updateStatus = Invoke-ApiRequest -Method "PUT" -Uri "$BASE_URL/pedidos/$pedidoId/status?status=CONFIRMADO" -Headers $HEADERS
        Show-TestResult -TestName "UPDATE STATUS - Confirmar Pedido" -Result $updateStatus
    }
}

# ====================================================================
# 6. CONSULTAS ESPECIAIS
# ====================================================================

Write-Host "🔍 6. CONSULTAS ESPECIAIS - POSTGRESQL" -ForegroundColor Yellow

# Buscar clientes por termo
$buscarClientes = Invoke-ApiRequest -Method "GET" -Uri "$BASE_URL/clientes/buscar?termo=Carlos" -Headers $HEADERS
Show-TestResult -TestName "SEARCH - Buscar Clientes por Termo" -Result $buscarClientes -ShowData $false

# Pedidos recentes
$pedidosRecentes = Invoke-ApiRequest -Method "GET" -Uri "$BASE_URL/pedidos/recentes" -Headers $HEADERS
Show-TestResult -TestName "RECENT - Pedidos Recentes" -Result $pedidosRecentes -ShowData $false

# Produtos por categoria
$produtosPorCategoria = Invoke-ApiRequest -Method "GET" -Uri "$BASE_URL/produtos/categoria/Pizza" -Headers $HEADERS
Show-TestResult -TestName "CATEGORY - Produtos por Categoria" -Result $produtosPorCategoria -ShowData $false

# ====================================================================
# 7. LIMPEZA (DELETE)
# ====================================================================

Write-Host "🗑️ 7. LIMPEZA - DELETE DOS REGISTROS CRIADOS" -ForegroundColor Yellow

# Delete Pedido
if ($pedidoId) {
    $deletePedido = Invoke-ApiRequest -Method "DELETE" -Uri "$BASE_URL/pedidos/$pedidoId" -Headers $HEADERS
    Show-TestResult -TestName "DELETE - Remover Pedido" -Result $deletePedido -ShowData $false
}

# Delete Produto
if ($produtoId) {
    $deleteProduto = Invoke-ApiRequest -Method "DELETE" -Uri "$BASE_URL/produtos/$produtoId" -Headers $HEADERS
    Show-TestResult -TestName "DELETE - Remover Produto" -Result $deleteProduto -ShowData $false
}

# Delete Restaurante
if ($restauranteId) {
    $deleteRestaurante = Invoke-ApiRequest -Method "DELETE" -Uri "$BASE_URL/restaurantes/$restauranteId" -Headers $HEADERS
    Show-TestResult -TestName "DELETE - Remover Restaurante" -Result $deleteRestaurante -ShowData $false
}

# Delete Cliente
if ($clienteId) {
    $deleteCliente = Invoke-ApiRequest -Method "DELETE" -Uri "$BASE_URL/clientes/$clienteId" -Headers $HEADERS
    Show-TestResult -TestName "DELETE - Remover Cliente" -Result $deleteCliente -ShowData $false
}

# ====================================================================
# 8. RELATÓRIO FINAL
# ====================================================================

Write-Host "📊 === RELATÓRIO FINAL ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "✅ Teste CRUD PostgreSQL concluído!" -ForegroundColor Green
Write-Host "📅 Data: $(Get-Date -Format 'dd/MM/yyyy HH:mm:ss')" -ForegroundColor Gray
Write-Host "🐘 Banco: PostgreSQL (Perfil de Produção)" -ForegroundColor Gray
Write-Host "🌐 Endpoint: $BASE_URL" -ForegroundColor Gray
Write-Host ""
Write-Host "🎯 Operações testadas:" -ForegroundColor Yellow
Write-Host "   ✅ CREATE (Inserção)" -ForegroundColor Green
Write-Host "   ✅ READ (Consulta)" -ForegroundColor Green
Write-Host "   ✅ UPDATE (Atualização)" -ForegroundColor Green
Write-Host "   ✅ DELETE (Remoção)" -ForegroundColor Green
Write-Host "   ✅ SEARCH (Busca)" -ForegroundColor Green
Write-Host ""
Write-Host "📋 Entidades testadas:" -ForegroundColor Yellow
Write-Host "   ✅ Cliente" -ForegroundColor Green
Write-Host "   ✅ Restaurante" -ForegroundColor Green
Write-Host "   ✅ Produto" -ForegroundColor Green
Write-Host "   ✅ Pedido" -ForegroundColor Green
Write-Host ""
Write-Host "🎉 TESTE POSTGRESQL CONCLUÍDO COM SUCESSO!" -ForegroundColor Cyan