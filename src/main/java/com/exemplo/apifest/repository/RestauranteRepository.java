package com.exemplo.apifest.repository;

import com.exemplo.apifest.model.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {
    List<Restaurante> findByAtivoTrue();
    List<Restaurante> findByNomeContainingIgnoreCase(String nome);
}