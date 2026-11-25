package com.example.compras.controller;

import java.util.List;
import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.compras.dto.CompraDTO;
import com.example.compras.model.Compra;
import com.example.compras.service.CompraService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/compras")
public class CompraController {

    private final CompraService compraService;

    public CompraController(CompraService compraService){
        this.compraService = compraService;
    }

    /*##-------------------------------METODOS GET-------------------------------##*/

    //obtiene todas las compras
    @GetMapping
    @ApiResponse(responseCode = "200", description = "Lista de compras")
    public ResponseEntity<List<Compra>> obtenerCompras(){
        return ResponseEntity.ok(compraService.obtenerCompras());
    }

    //obtiene una compra por id (la usa la boleta)
    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Compra encontrada")
    @ApiResponse(responseCode = "404", description = "La compra no existe")
    public ResponseEntity<Compra> obtenerCompraById(@PathVariable("id") Long id){
        return ResponseEntity.ok(compraService.obtenerCompraById(id));
    }

    //obtiene las compras de un usuario (historial del admin)
    @GetMapping("/usuario/{usuarioId}")
     @ApiResponse(responseCode = "200", description = "Lista de compras del usuario (vacia si no tiene)")
    public ResponseEntity<List<Compra>> obtenerComprasByUsuario(@PathVariable("usuarioId") Long usuarioId){
        return ResponseEntity.ok(compraService.obtenerComprasByUsuario(usuarioId));
    }

    /*##-------------------------------METODOS POST-------------------------------##*/

    //registra una compra
    @PostMapping
    @ApiResponse(responseCode = "201", description = "Compra creada (header Location con la URL del recurso)")
    @ApiResponse(responseCode = "400", description = "Datos invalidos")
    @ApiResponse(responseCode = "403", description = "El correo no tiene cuenta registrada")
    @ApiResponse(responseCode = "503", description = "El microservicio de usuarios no responde")
    public ResponseEntity<Compra> registrarCompra(@Valid @RequestBody CompraDTO dto){
        Compra nueva = compraService.registrarCompra(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(nueva.getId()).toUri();
        return ResponseEntity.created(location).body(nueva);
    }
}