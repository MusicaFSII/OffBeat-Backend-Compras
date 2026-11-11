package com.example.compras.exception;

public class UsuarioNoRegistradoException extends RuntimeException {
    public UsuarioNoRegistradoException(){
        super("Debes tener una cuenta para realizar compras");
    }
}