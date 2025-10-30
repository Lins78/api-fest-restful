package com.exemplo.apifest.controller;

import com.exemplo.apifest.model.Pedido;
import com.exemplo.apifest.model.Cliente;
import com.exemplo.apifest.repository.PedidoRepository;
import com.exemplo.apifest.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    @GetMapping("/ativos")
    public List<Pedido> listarAtivos() {
        return pedidoRepository.findByAtivoTrue();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        return pedido.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Pedido> buscarPorCliente(@PathVariable Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    @PostMapping
    public ResponseEntity<Pedido> criar(@RequestBody PedidoRequest pedidoRequest) {
        Optional<Cliente> cliente = clienteRepository.findById(pedidoRequest.getClienteId());
        if (cliente.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Pedido pedido = new Pedido();
        pedido.setDescricao(pedidoRequest.getDescricao());
        pedido.setValor(pedidoRequest.getValor());
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setCliente(cliente.get());
        pedido.setAtivo(true);
        
        return ResponseEntity.ok(pedidoRepository.save(pedido));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> atualizar(@PathVariable Long id, @RequestBody PedidoRequest pedidoRequest) {
        return pedidoRepository.findById(id)
                .map(pedido -> {
                    pedido.setDescricao(pedidoRequest.getDescricao());
                    pedido.setValor(pedidoRequest.getValor());
                    if (pedidoRequest.getClienteId() != null) {
                        clienteRepository.findById(pedidoRequest.getClienteId())
                                .ifPresent(pedido::setCliente);
                    }
                    return ResponseEntity.ok(pedidoRepository.save(pedido));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        return pedidoRepository.findById(id)
                .map(pedido -> {
                    pedido.inativar();
                    pedidoRepository.save(pedido);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Classe auxiliar para receber dados do pedido
    public static class PedidoRequest {
        private String descricao;
        private Double valor;
        private Long clienteId;

        // Getters e Setters
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
        
        public Double getValor() { return valor; }
        public void setValor(Double valor) { this.valor = valor; }
        
        public Long getClienteId() { return clienteId; }
        public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    }
}