package com.library.frontms.security;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtPropagationInterceptor implements ClientHttpRequestInterceptor {

    private final JwtSessionTokenProvider tokenProvider;

    public JwtPropagationInterceptor(JwtSessionTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        String token = tokenProvider.getToken();
        if (token != null) {
            request.getHeaders().setBearerAuth(token);
        }
        return execution.execute(request, body);
    }
}
