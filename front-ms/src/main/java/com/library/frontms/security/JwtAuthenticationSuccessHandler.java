package com.library.frontms.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public static final String SESSION_ATTR_TOKEN = "JWT_TOKEN";

    private final JwtService jwtService;

    public JwtAuthenticationSuccessHandler(JwtService jwtService) {
        this.jwtService = jwtService;
        setDefaultTargetUrl("/livros");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException, ServletException {
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .map(authority -> authority.replace("ROLE_", ""))
                .orElseThrow();
        String token = jwtService.gerarToken(authentication.getName(), role);
        request.getSession().setAttribute(SESSION_ATTR_TOKEN, token);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
