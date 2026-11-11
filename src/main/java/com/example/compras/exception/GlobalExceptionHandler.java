package com.example.compras.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //404: la compra no existe
    @ExceptionHandler(CompraNoEncontradaException.class)
    public ResponseEntity<Map<String, String>> noEncontrada(CompraNoEncontradaException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }

    //400: fallo alguna validacion del DTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacion(MethodArgumentNotValidException e){
        String mensaje = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .findFirst()
            .orElse("Datos invalidos");
        return ResponseEntity.badRequest().body(Map.of("error", mensaje));
    }

    //403: el correo no tiene cuenta
    @ExceptionHandler(UsuarioNoRegistradoException.class)
    public ResponseEntity<Map<String, String>> usuarioNoRegistrado(UsuarioNoRegistradoException e){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
    }

    //503: el microservicio de usuarios no responde
    @ExceptionHandler(ServicioNoDisponibleException.class)
    public ResponseEntity<Map<String, String>> servicioNoDisponible(ServicioNoDisponibleException e){
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("error", e.getMessage()));
    }
}