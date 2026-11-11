package com.example.compras.client;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.compras.dto.UsuarioRespuestaDTO;
import com.example.compras.exception.ServicioNoDisponibleException;
import com.example.compras.exception.UsuarioNoRegistradoException;

@Component
public class UsuarioClient {

    private final WebClient webClient;

    public UsuarioClient(@Qualifier("usuariosWebClient") WebClient webClient){
        this.webClient = webClient;
    }

    //devuelve el id del usuario con ese correo; lanza 403 si no tiene cuenta y 503 si usuarios no responde
    public Long obtenerIdPorCorreo(String correo){
        try {
            UsuarioRespuestaDTO usuario = webClient.get()
                .uri("/api/usuarios/correo/{correo}", correo)
                .retrieve()
                .bodyToMono(UsuarioRespuestaDTO.class)
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