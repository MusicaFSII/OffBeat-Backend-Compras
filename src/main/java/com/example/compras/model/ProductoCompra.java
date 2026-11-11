package com.example.compras.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoCompra {

    //id o codigo del producto en el microservicio de productos
    @Column(name="producto_id")
    private String productoId;

    @Column(name="nombre_producto", nullable = false)
    private String nombre;

    @Column(name="precio", nullable = false)
    private Double precio;
}