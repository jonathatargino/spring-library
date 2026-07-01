package com.library.frontms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErroController {

    @GetMapping("/erro/acesso-negado")
    public String acessoNegado(Model model) {
        model.addAttribute("mensagemErro", "Acesso restrito a bibliotecários.");
        return "error/generico";
    }
}
