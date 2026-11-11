package com.example.compras.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.compras.dto.CompraDTO;
import com.example.compras.model.Compra;
import com.example.compras.service.CompraService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/compras")
@CrossOrigin(origins = "*")
public class CompraController {

    private final CompraService compraService;

    public CompraController(CompraService compraService){
        this.compraService = compraService;
    }

    /*##-------------------------------METODOS GET-------------------------------##*/

    //obtiene todas las compras
    @GetMapping
    public ResponseEntity<List<Compra>> obtenerCompras(){
        return ResponseEntity.ok(compraService.obtenerCompras());
    }

    //obtiene una compra por id (la usa la boleta)
    @GetMapping("/{id}")
    public ResponseEntity<Compra> obtenerCompraById(@PathVariable("id") Long id){
        return ResponseEntity.ok(compraService.obtenerCompraById(id));
    }

    //obtiene las compras de un usuario (historial del admin)
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Compra>> obtenerComprasByUsuario(@PathVariable("usuarioId") Long usuarioId){
        return ResponseEntity.ok(compraService.obtenerComprasByUsuario(usuarioId));
    }

    /*##-------------------------------METODOS POST-------------------------------##*/

    //registra una compra
    @PostMapping("/guardar")
    public ResponseEntity<Compra> registrarCompra(@Valid @RequestBody CompraDTO dto){
        Compra nueva = compraService.registrarCompra(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }
}