package com.example.compras.exception;
public class ProductoNoEncontradoException extends RuntimeException {
    public ProductoNoEncontradoException(String codigo){
        super("El producto " + codigo + " ya no esta disponible");
    }
}