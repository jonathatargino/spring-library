package com.library.frontms.config;

import com.library.frontms.security.JwtPropagationInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient autorClient(@Value("${autor.ms.url}") String autorMsUrl,
                                   JwtPropagationInterceptor jwtPropagationInterceptor) {
        return RestClient.builder().baseUrl(autorMsUrl)
                .requestInterceptor(jwtPropagationInterceptor)
                .build();
    }

    @Bean
    public RestClient livroClient(@Value("${livro.ms.url}") String livroMsUrl,
                                   JwtPropagationInterceptor jwtPropagationInterceptor) {
        return RestClient.builder().baseUrl(livroMsUrl)
                .requestInterceptor(jwtPropagationInterceptor)
                .build();
    }
}
