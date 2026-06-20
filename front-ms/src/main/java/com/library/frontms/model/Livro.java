package com.library.frontms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class Livro {

    private Long id;

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 150, message = "Título deve ter no máximo 150 caracteres")
    private String titulo;

    @NotBlank(message = "Gênero é obrigatório")
    @Size(max = 60, message = "Gênero deve ter no máximo 60 caracteres")
    private String genero;

    @NotNull(message = "Ano de publicação é obrigatório")
    @Positive(message = "Ano de publicação deve ser positivo")
    private Integer anoPublicacao;

    private Boolean disponivel = true;

    @NotNull(message = "Autor é obrigatório")
    private Long autorId;

    @JsonIgnore
    private String nomeAutor;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public Integer getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(Integer anoPublicacao) { this.anoPublicacao = anoPublicacao; }

    public Boolean getDisponivel() { return disponivel; }
    public void setDisponivel(Boolean disponivel) { this.disponivel = disponivel; }

    public Long getAutorId() { return autorId; }
    public void setAutorId(Long autorId) { this.autorId = autorId; }

    public String getNomeAutor() { return nomeAutor; }
    public void setNomeAutor(String nomeAutor) { this.nomeAutor = nomeAutor; }
}
