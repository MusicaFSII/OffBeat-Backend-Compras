package com.example.compras.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.compras.client.UsuarioClient;
import com.example.compras.dto.CompraDTO;
import com.example.compras.exception.CompraNoEncontradaException;
import com.example.compras.mapper.CompraMapper;
import com.example.compras.model.Compra;
import com.example.compras.repository.CompraRepository;
import com.example.compras.dto.ItemCompraDTO;
import com.example.compras.dto.ProductoRespuestaDTO;
import com.example.compras.exception.ProductoNoEncontradoException;
import com.example.compras.exception.UsuarioNoRegistradoException;
import com.example.compras.model.ProductoCompra;

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
        Long usuarioId;
        try {
            usuarioId = usuarioClient.obtenerIdPorCorreo(dto.getCorreo());
        } catch (Exception e) {
            e.printStackTrace();
            throw new UsuarioNoRegistradoException();
        }

        Compra compra = compraMapper.toEntity(dto);
        compra.setUsuarioId(usuarioId);

        double total = 0.0;
        if (compra.getDetalles() != null) {
            for (int i = 0; i < compra.getDetalles().size(); i++) {
                var detalle = compra.getDetalles().get(i);
                var itemDto = dto.getProductos().get(i);

                if (detalle.getProducto() == null) {
                    detalle.setProducto(new ProductoCompra());
                }

                detalle.getProducto().setPrecio(itemDto.getPrecio());
                detalle.getProducto().setNombre(itemDto.getNombre());
                detalle.getProducto().setProductoId(itemDto.getProductoId());

                total += itemDto.getPrecio() * detalle.getCantidad();
            }
        }

        compra.setTotal(total);
        compra.setFecha(LocalDateTime.now());

        return compraRepo.save(compra);
    }
}