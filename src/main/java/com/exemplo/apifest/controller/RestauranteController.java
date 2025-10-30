package com.exemplo.apifest.controller;

import com.exemplo.apifest.model.Restaurante;
import com.exemplo.apifest.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/restaurantes")
public class RestauranteController {

    @Autowired
    private RestauranteRepository restauranteRepository;

    @GetMapping
    public List<Restaurante> listarTodos() {
        return restauranteRepository.findAll();
    }

    @GetMapping("/ativos")
    public List<Restaurante> listarAtivos() {
        return restauranteRepository.findByAtivoTrue();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurante> buscarPorId(@PathVariable Long id) {
        Optional<Restaurante> restaurante = restauranteRepository.findById(id);
        return restaurante.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public List<Restaurante> buscarPorNome(@RequestParam String nome) {
        return restauranteRepository.findByNomeContainingIgnoreCase(nome);
    }

    @PostMapping
    public Restaurante criar(@RequestBody Restaurante restaurante) {
        restaurante.setAtivo(true);
        return restauranteRepository.save(restaurante);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Restaurante> atualizar(@PathVariable Long id, @RequestBody Restaurante restauranteAtualizado) {
        return restauranteRepository.findById(id)
                .map(restaurante -> {
                    restaurante.setNome(restauranteAtualizado.getNome());
                    restaurante.setEndereco(restauranteAtualizado.getEndereco());
                    restaurante.setTelefone(restauranteAtualizado.getTelefone());
                    return ResponseEntity.ok(restauranteRepository.save(restaurante));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        return restauranteRepository.findById(id)
                .map(restaurante -> {
                    restaurante.inativar();
                    restauranteRepository.save(restaurante);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}