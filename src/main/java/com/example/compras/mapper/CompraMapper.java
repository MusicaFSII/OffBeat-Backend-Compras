package com.example.compras.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.compras.dto.CompraDTO;
import com.example.compras.dto.ItemCompraDTO;
import com.example.compras.model.Compra;
import com.example.compras.model.DetalleCompra;
import com.example.compras.model.ProductoCompra;

@Component
public class CompraMapper {

    public Compra toEntity(CompraDTO dto){
        if(dto == null){
            return null;
        }

        Compra compra = new Compra();
        compra.setCorreo(dto.getCorreo());
        compra.setNombre(dto.getNombre());
        compra.setApellido(dto.getApellido());
        compra.setTelefono(dto.getTelefono());
        compra.setCalle(dto.getCalle());
        compra.setDepo(dto.getDepo());
        compra.setRegion(dto.getRegion());
        compra.setComuna(dto.getComuna());
        compra.setIndicacion(dto.getIndicacion());

        List<DetalleCompra> detalles = dto.getProductos().stream()
            .map(this::toDetalle)
            .collect(Collectors.toCollection(ArrayList::new));
        compra.setDetalles(detalles);

        return compra;
    }

        private DetalleCompra toDetalle(ItemCompraDTO item){
        ProductoCompra producto = new ProductoCompra(
            item.getCodigo(),
            item.getNombre(),
            item.getPrecio()
        );

        DetalleCompra detalle = new DetalleCompra();
        detalle.setProducto(producto);
        detalle.setCantidad(item.getCantidad());
        return detalle;
    }
}