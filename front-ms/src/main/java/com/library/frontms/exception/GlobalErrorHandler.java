package com.library.frontms.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;

@ControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(RestClientException.class)
    public String handleRestClientError(RestClientException ex, Model model) {
        model.addAttribute("mensagemErro", "Um serviço está temporariamente indisponível. Tente novamente mais tarde.");
        return "error/generico";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericError(Exception ex, Model model) {
        model.addAttribute("mensagemErro", "Ocorreu um erro inesperado. Tente novamente.");
        return "error/generico";
    }
}
