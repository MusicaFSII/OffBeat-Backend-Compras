package com.example.compras.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;                           

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

    //400: el JSON viene mal formado o con tipos incorrectos
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> jsonInvalido(HttpMessageNotReadableException e){
        return ResponseEntity.badRequest().body(Map.of("error", "El cuerpo de la solicitud no es valido"));
    }

    //400: un parametro de la URL no tiene el tipo esperado (ej: /compras/abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> tipoInvalido(MethodArgumentTypeMismatchException e){
        return ResponseEntity.badRequest().body(Map.of("error", "El parametro '" + e.getName() + "' no es valido"));
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

    //400: el producto no existe
    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> productoNoEncontrado(ProductoNoEncontradoException e){
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}