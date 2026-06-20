package com.library.frontms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient autorClient(@Value("${autor.ms.url}") String autorMsUrl) {
        return RestClient.builder().baseUrl(autorMsUrl).build();
    }

    @Bean
    public RestClient livroClient(@Value("${livro.ms.url}") String livroMsUrl) {
        return RestClient.builder().baseUrl(livroMsUrl).build();
    }
}
