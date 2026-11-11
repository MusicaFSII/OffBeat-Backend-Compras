package com.example.compras.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.compras.client.UsuarioClient;
import com.example.compras.dto.CompraDTO;
import com.example.compras.exception.CompraNoEncontradaException;
import com.example.compras.mapper.CompraMapper;
import com.example.compras.model.Compra;
import com.example.compras.repository.CompraRepository;

@Service
public class CompraService {

    private final CompraRepository compraRepo;
    private final CompraMapper compraMapper;
    private final UsuarioClient usuarioClient;

    public CompraService(CompraRepository compraRepo, CompraMapper compraMapper, UsuarioClient usuarioClient){
        this.compraRepo = compraRepo;
        this.compraMapper = compraMapper;
        this.usuarioClient = usuarioClient;
    }

    //obtiene todas las compras ordenada de la mas reciente a la mas antigua
    public List<Compra> obtenerCompras(){
        return compraRepo.findAllByOrderByFechaDesc();
    }

    //obtiene una compra con su detalle o 404 si no existe
    public Compra obtenerCompraById(Long id){
        return compraRepo.findById(id).orElseThrow(CompraNoEncontradaException::new);
    }

    //obtiene las compras de un usuario
    public List<Compra> obtenerComprasByUsuario(Long usuarioId){
        return compraRepo.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    //registra una compra, guarda la cabecera y sus detalles
    public Compra registrarCompra(CompraDTO dto){
        //si el correo no tiene cuenta lanza 403 y la compra no se guarda
        Long usuarioId = usuarioClient.obtenerIdPorCorreo(dto.getCorreo());

        Compra compra = compraMapper.toEntity(dto);
        compra.setUsuarioId(usuarioId);

        double total = compra.getDetalles().stream()
            .mapToDouble(d -> d.getProducto().getPrecio() * d.getCantidad())
            .sum();
        compra.setTotal(total);
        compra.setFecha(LocalDateTime.now());

        return compraRepo.save(compra);
    }
}