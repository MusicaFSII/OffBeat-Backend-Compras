package com.example.compras.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    //llama al microservicio de usuarios
    @Bean
    public WebClient usuariosWebClient(@Value("${usuarios.service.url}") String url){
        return WebClient.builder().baseUrl(url).build();
    }

    //llama al microservicio de productos
    @Bean
    public WebClient productosWebClient(@Value("${productos.service.url}") String url){
        return WebClient.builder().baseUrl(url).build();
    }
}