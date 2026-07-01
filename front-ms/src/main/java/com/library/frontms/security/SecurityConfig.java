package com.library.frontms.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                     JwtAuthenticationSuccessHandler successHandler) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/erro/**", "/css/**", "/js/**", "/webjars/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/", "/autores", "/livros", "/livros/*").authenticated()
                        .requestMatchers("/autores/novo", "/autores/*/editar", "/autores/*/excluir",
                                "/livros/novo", "/livros/*/editar", "/livros/*/excluir").hasRole("BIBLIOTECARIO")
                        .requestMatchers(HttpMethod.POST, "/autores", "/autores/*", "/livros", "/livros/*")
                        .hasRole("BIBLIOTECARIO")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .permitAll())
                .exceptionHandling(eh -> eh.accessDeniedPage("/erro/acesso-negado"));
        return http.build();
    }
}
