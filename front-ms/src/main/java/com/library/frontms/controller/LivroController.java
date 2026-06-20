package com.library.frontms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.frontms.model.Autor;
import com.library.frontms.model.Livro;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/livros")
public class LivroController {

    private final RestClient livroClient;
    private final RestClient autorClient;
    private final ObjectMapper objectMapper;

    public LivroController(@Qualifier("livroClient") RestClient livroClient,
                           @Qualifier("autorClient") RestClient autorClient,
                           ObjectMapper objectMapper) {
        this.livroClient = livroClient;
        this.autorClient = autorClient;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) Boolean disponivel, Model model) {
        try {
            String uri = disponivel != null ? "/api/livros?disponivel=" + disponivel : "/api/livros";
            List<Livro> livros = livroClient.get().uri(uri)
                    .retrieve().body(new ParameterizedTypeReference<>() {});

            if (livros != null) {
                for (Livro livro : livros) {
                    livro.setNomeAutor(resolverNomeAutor(livro.getAutorId()));
                }
                model.addAttribute("livros", livros);
            } else {
                model.addAttribute("livros", List.of());
            }
        } catch (Exception e) {
            model.addAttribute("erro", "Serviço de livros temporariamente indisponível.");
            model.addAttribute("livros", List.of());
        }
        model.addAttribute("disponivel", disponivel);
        return "livros/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("livro", new Livro());
        carregarAutores(model);
        return "livros/formulario";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Livro livro = livroClient.get().uri("/api/livros/" + id)
                    .retrieve().body(Livro.class);
            if (livro != null) {
                livro.setNomeAutor(resolverNomeAutor(livro.getAutorId()));
                model.addAttribute("livro", livro);
            }
            return "livros/detalhe";
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                model.addAttribute("mensagemErro", "Livro não encontrado.");
                return "error/generico";
            }
            throw e;
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Livro livro = livroClient.get().uri("/api/livros/" + id)
                    .retrieve().body(Livro.class);
            model.addAttribute("livro", livro);
            carregarAutores(model);
            return "livros/formulario";
        } catch (RestClientResponseException e) {
            redirectAttributes.addFlashAttribute("erro", "Livro não encontrado.");
            return "redirect:/livros";
        }
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("livro") Livro livro, BindingResult result,
                        Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            carregarAutores(model);
            return "livros/formulario";
        }
        if (livro.getDisponivel() == null) livro.setDisponivel(false);
        try {
            livroClient.post().uri("/api/livros")
                    .body(livro).retrieve().toBodilessEntity();
            redirectAttributes.addFlashAttribute("mensagem", "Livro cadastrado com sucesso!");
            return "redirect:/livros";
        } catch (RestClientResponseException e) {
            model.addAttribute("erro", extractErro(e.getResponseBodyAsString()));
            carregarAutores(model);
            return "livros/formulario";
        }
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("livro") Livro livro, BindingResult result,
                            Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            carregarAutores(model);
            return "livros/formulario";
        }
        if (livro.getDisponivel() == null) livro.setDisponivel(false);
        try {
            livroClient.put().uri("/api/livros/" + id)
                    .body(livro).retrieve().toBodilessEntity();
            redirectAttributes.addFlashAttribute("mensagem", "Livro atualizado com sucesso!");
            return "redirect:/livros";
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                redirectAttributes.addFlashAttribute("erro", "Livro não encontrado.");
                return "redirect:/livros";
            }
            model.addAttribute("erro", extractErro(e.getResponseBodyAsString()));
            carregarAutores(model);
            return "livros/formulario";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            livroClient.delete().uri("/api/livros/" + id)
                    .retrieve().toBodilessEntity();
            redirectAttributes.addFlashAttribute("mensagem", "Livro excluído com sucesso!");
        } catch (RestClientResponseException e) {
            redirectAttributes.addFlashAttribute("erro", "Livro não encontrado ou não pôde ser excluído.");
        }
        return "redirect:/livros";
    }

    private String resolverNomeAutor(Long autorId) {
        if (autorId == null) return "Autor removido";
        try {
            Autor autor = autorClient.get().uri("/api/autores/" + autorId)
                    .retrieve().body(Autor.class);
            return autor != null ? autor.getNome() : "Autor removido";
        } catch (Exception e) {
            return "Autor removido";
        }
    }

    private void carregarAutores(Model model) {
        try {
            List<Autor> autores = autorClient.get().uri("/api/autores")
                    .retrieve().body(new ParameterizedTypeReference<>() {});
            model.addAttribute("autores", autores != null ? autores : List.of());
        } catch (Exception e) {
            model.addAttribute("autores", List.of());
            model.addAttribute("avisoAutores", "Não foi possível carregar a lista de autores.");
        }
    }

    @SuppressWarnings("unchecked")
    private String extractErro(String responseBody) {
        try {
            Map<String, Object> map = objectMapper.readValue(responseBody, Map.class);
            Object erro = map.get("erro");
            return erro != null ? erro.toString() : "Erro ao processar requisição.";
        } catch (Exception ex) {
            return "Erro ao processar requisição.";
        }
    }
}
