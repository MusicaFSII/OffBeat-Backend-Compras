package com.example.compras.client;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.compras.dto.ProductoRespuestaDTO;
import com.example.compras.exception.ServicioNoDisponibleException;

@Component
public class ProductoClient {

    private final WebClient webClient;

    public ProductoClient(@Qualifier("productosWebClient") WebClient webClient){
        this.webClient = webClient;
    }

    public Map<String, ProductoRespuestaDTO> obtenerPorCodigos(List<String> codigos){
        try {
            List<ProductoRespuestaDTO> lista = webClient.post()
                .uri("/api/v1/productos/lista")
                .bodyValue(codigos)
                .retrieve()
                .bodyToFlux(ProductoRespuestaDTO.class)
                .collectList()
                .block();
            return lista.stream().collect(Collectors.toMap(ProductoRespuestaDTO::getCodigo, p -> p));
        } catch (WebClientRequestException | WebClientResponseException e) {
            throw new ServicioNoDisponibleException("No se pudieron verificar los precios, intenta nuevamente en unos minutos");
        }
    }
}