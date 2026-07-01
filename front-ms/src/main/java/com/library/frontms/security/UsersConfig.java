package com.library.frontms.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UsersConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder,
            @Value("${app.usuarios.bibliotecario.usuario}") String bibliotecarioUsuario,
            @Value("${app.usuarios.bibliotecario.senha}") String bibliotecarioSenha,
            @Value("${app.usuarios.usuario.usuario}") String usuarioUsuario,
            @Value("${app.usuarios.usuario.senha}") String usuarioSenha) {
        return new InMemoryUserDetailsManager(
                User.withUsername(bibliotecarioUsuario)
                        .password(passwordEncoder.encode(bibliotecarioSenha))
                        .roles("BIBLIOTECARIO")
                        .build(),
                User.withUsername(usuarioUsuario)
                        .password(passwordEncoder.encode(usuarioSenha))
                        .roles("USUARIO")
                        .build());
    }
}
