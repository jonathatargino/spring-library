package com.library.autorms.service;

import com.library.autorms.model.Autor;
import com.library.autorms.repository.AutorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutorService {

    private final AutorRepository repository;

    public AutorService(AutorRepository repository) {
        this.repository = repository;
    }

    public List<Autor> listarTodos() {
        return repository.findAll();
    }

    public Autor buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Autor não encontrado: " + id));
    }

    public Autor criar(Autor autor) {
        return repository.save(autor);
    }

    public Autor atualizar(Long id, Autor dados) {
        Autor autor = buscarPorId(id);
        autor.setNome(dados.getNome());
        autor.setNacionalidade(dados.getNacionalidade());
        autor.setAnoNascimento(dados.getAnoNascimento());
        return repository.save(autor);
    }

    public void excluir(Long id) {
        buscarPorId(id);
        repository.deleteById(id);
    }
}
