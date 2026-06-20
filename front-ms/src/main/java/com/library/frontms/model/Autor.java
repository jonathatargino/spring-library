package com.library.frontms.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class Autor {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "Nacionalidade é obrigatória")
    @Size(max = 80, message = "Nacionalidade deve ter no máximo 80 caracteres")
    private String nacionalidade;

    @NotNull(message = "Ano de nascimento é obrigatório")
    @Positive(message = "Ano de nascimento deve ser positivo")
    private Integer anoNascimento;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getNacionalidade() { return nacionalidade; }
    public void setNacionalidade(String nacionalidade) { this.nacionalidade = nacionalidade; }

    public Integer getAnoNascimento() { return anoNascimento; }
    public void setAnoNascimento(Integer anoNascimento) { this.anoNascimento = anoNascimento; }
}
