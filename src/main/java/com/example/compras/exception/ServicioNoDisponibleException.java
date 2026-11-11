package com.example.compras.exception;

public class ServicioNoDisponibleException extends RuntimeException {
    public ServicioNoDisponibleException(){
        super("No se pudo verificar tu cuenta, intenta nuevamente en unos minutos");
    }
}