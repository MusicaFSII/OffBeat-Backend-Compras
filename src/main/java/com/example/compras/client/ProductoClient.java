package com.example.compras.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ProductoClient {

    @Bean(name = "productoApiWebClient")
    public WebClient productoApiWebClient() {
        return WebClient.builder()
            .baseUrl("http://localhost:8082")
            .build();
    }
}
