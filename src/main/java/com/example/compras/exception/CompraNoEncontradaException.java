package com.example.compras.exception;

public class CompraNoEncontradaException extends RuntimeException {
    public CompraNoEncontradaException(){
        super("Compra no encontrada");
    }
}