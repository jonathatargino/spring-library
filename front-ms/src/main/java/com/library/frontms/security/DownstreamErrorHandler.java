package com.library.frontms.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Component
public class DownstreamErrorHandler {

    public Optional<String> tratar(RestClientResponseException e, HttpServletRequest request,
                                    RedirectAttributes redirectAttributes) {
        int status = e.getStatusCode().value();
        if (status == 401) {
            request.getSession().invalidate();
            redirectAttributes.addFlashAttribute("erro", "Sua sessão expirou. Faça login novamente.");
            return Optional.of("redirect:/login");
        }
        if (status == 403) {
            redirectAttributes.addFlashAttribute("erro", "Acesso restrito a bibliotecários.");
            return Optional.of("redirect:/");
        }
        return Optional.empty();
    }
}
