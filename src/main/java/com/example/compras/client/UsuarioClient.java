package com.example.compras.client;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.compras.exception.ServicioNoDisponibleException;
import com.example.compras.exception.UsuarioNoRegistradoException;
import lombok.Data;

@Component
public class UsuarioClient {

    private final WebClient webClient;

    public UsuarioClient(@Qualifier("usuariosWebClient") WebClient webClient){
        this.webClient = webClient;
    }

    // Clase interna temporal o DTO que matchee exactamente el id que devuelve el usuario
    @Data
    private static class UsuarioTempDTO {
        private Long id;
        private String correo;
    }

    //devuelve el id del usuario con ese correo
    public Long obtenerIdPorCorreo(String correo){
        try {
            UsuarioTempDTO usuario = webClient.get()
                .uri("/api/usuarios/correo/{correo}", correo)
                .retrieve()
                .bodyToMono(UsuarioTempDTO.class)
                .block(Duration.ofSeconds(3));

            if(usuario == null || usuario.getId() == null){
                throw new UsuarioNoRegistradoException();
            }
            return usuario.getId();
        } catch (WebClientResponseException.NotFound e) {
            throw new UsuarioNoRegistradoException();
        } catch (WebClientRequestException | WebClientResponseException | IllegalStateException e) {
            throw new ServicioNoDisponibleException();
        }
    }
}