package com.example.compras.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Compras")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_compra")
    private Long id;

    //id del usuario en el microservicio de usuarios
    @Column(name="usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name="correo", nullable = false)
    private String correo;

    @Column(name="nombre", nullable = false)
    private String nombre;

    @Column(name="apellido", nullable = false)
    private String apellido;

    @Column(name="telefono", nullable = false)
    private String telefono;

    @Column(name="calle", nullable = false)
    private String calle;

    @Column(name="depo")
    private String depo;

    @Column(name="region", nullable = false)
    private String region;

    @Column(name="comuna", nullable = false)
    private String comuna;

    @Column(name="indicacion")
    private String indicacion;

    @Column(name="total", nullable = false)
    private Double total;

    @Column(name="fecha", nullable = false)
    private LocalDateTime fecha;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name="compra_id", nullable = false)
    private List<DetalleCompra> detalles = new ArrayList<>();
}