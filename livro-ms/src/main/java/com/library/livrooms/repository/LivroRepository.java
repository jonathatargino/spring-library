package com.library.livrooms.repository;

import com.library.livrooms.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    List<Livro> findByDisponivel(Boolean disponivel);
}
