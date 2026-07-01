package com.library.frontms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.frontms.model.Autor;
import com.library.frontms.security.DownstreamErrorHandler;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Optional;

@Controller
@RequestMapping("/autores")
public class AutorController {

    private final RestClient autorClient;
    private final ObjectMapper objectMapper;
    private final DownstreamErrorHandler downstreamErrorHandler;

    public AutorController(@Qualifier("autorClient") RestClient autorClient, ObjectMapper objectMapper,
                            DownstreamErrorHandler downstreamErrorHandler) {
        this.autorClient = autorClient;
        this.objectMapper = objectMapper;
        this.downstreamErrorHandler = downstreamErrorHandler;
    }

    @GetMapping
    public String listar(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            List<Autor> autores = autorClient.get().uri("/api/autores")
                    .retrieve().body(new ParameterizedTypeReference<>() {});
            model.addAttribute("autores", autores != null ? autores : List.of());
        } catch (RestClientResponseException e) {
            Optional<String> redirecionamento = downstreamErrorHandler.tratar(e, request, redirectAttributes);
            if (redirecionamento.isPresent()) return redirecionamento.get();
            model.addAttribute("erro", "Serviço de autores temporariamente indisponível.");
            model.addAttribute("autores", List.of());
        } catch (Exception e) {
            model.addAttribute("erro", "Serviço de autores temporariamente indisponível.");
            model.addAttribute("autores", List.of());
        }
        return "autores/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("autor", new Autor());
        return "autores/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, HttpServletRequest request,
                         RedirectAttributes redirectAttributes) {
        try {
            Autor autor = autorClient.get().uri("/api/autores/" + id)
                    .retrieve().body(Autor.class);
            model.addAttribute("autor", autor);
            return "autores/formulario";
        } catch (RestClientResponseException e) {
            Optional<String> redirecionamento = downstreamErrorHandler.tratar(e, request, redirectAttributes);
            if (redirecionamento.isPresent()) return redirecionamento.get();
            redirectAttributes.addFlashAttribute("erro", "Autor não encontrado.");
            return "redirect:/autores";
        }
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("autor") Autor autor, BindingResult result,
                        Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "autores/formulario";
        }
        try {
            autorClient.post().uri("/api/autores")
                    .body(autor).retrieve().toBodilessEntity();
            redirectAttributes.addFlashAttribute("mensagem", "Autor cadastrado com sucesso!");
            return "redirect:/autores";
        } catch (RestClientResponseException e) {
            Optional<String> redirecionamento = downstreamErrorHandler.tratar(e, request, redirectAttributes);
            if (redirecionamento.isPresent()) return redirecionamento.get();
            model.addAttribute("erro", extractErro(e.getResponseBodyAsString()));
            return "autores/formulario";
        }
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("autor") Autor autor, BindingResult result,
                            Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "autores/formulario";
        }
        try {
            autorClient.put().uri("/api/autores/" + id)
                    .body(autor).retrieve().toBodilessEntity();
            redirectAttributes.addFlashAttribute("mensagem", "Autor atualizado com sucesso!");
            return "redirect:/autores";
        } catch (RestClientResponseException e) {
            Optional<String> redirecionamento = downstreamErrorHandler.tratar(e, request, redirectAttributes);
            if (redirecionamento.isPresent()) return redirecionamento.get();
            if (e.getStatusCode().value() == 404) {
                redirectAttributes.addFlashAttribute("erro", "Autor não encontrado.");
                return "redirect:/autores";
            }
            model.addAttribute("erro", extractErro(e.getResponseBodyAsString()));
            return "autores/formulario";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            autorClient.delete().uri("/api/autores/" + id)
                    .retrieve().toBodilessEntity();
            redirectAttributes.addFlashAttribute("mensagem", "Autor excluído com sucesso!");
        } catch (RestClientResponseException e) {
            Optional<String> redirecionamento = downstreamErrorHandler.tratar(e, request, redirectAttributes);
            if (redirecionamento.isPresent()) return redirecionamento.get();
            redirectAttributes.addFlashAttribute("erro", "Autor não encontrado ou não pôde ser excluído.");
        }
        return "redirect:/autores";
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
