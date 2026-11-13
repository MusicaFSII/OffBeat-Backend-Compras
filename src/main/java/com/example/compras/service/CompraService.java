package com.example.compras.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.compras.client.UsuarioClient;
import com.example.compras.dto.CompraDTO;
import com.example.compras.dto.ProdAgregarDTO;
import com.example.compras.exception.CompraNoEncontradaException;
import com.example.compras.mapper.CompraMapper;
import com.example.compras.model.Compra;
import com.example.compras.model.ProductoCompra;
import com.example.compras.repository.CompraRepository;

@Service
public class CompraService {

    private final CompraRepository compraRepo;
    private final CompraMapper compraMapper;
    private final UsuarioClient usuarioClient;
    private final WebClient productosWebClient; //Acceso a API de productos

    public CompraService(CompraRepository compraRepo, CompraMapper compraMapper, 
        UsuarioClient usuarioClient, @Qualifier("productosWebClient")WebClient productosWebClient){
        this.compraRepo = compraRepo;
        this.compraMapper = compraMapper;
        this.usuarioClient = usuarioClient;
        this.productosWebClient = productosWebClient; 
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

        List<String> codigos = compra.getDetalles().stream()
            .map(d -> d.getProducto().getCodigo())
            .distinct()
            .toList();
        List<ProdAgregarDTO> productos = obtenerProductosByCodigos(codigos);

        compra.getDetalles().forEach(detalle ->{
            String codigo = detalle.getProducto().getCodigo();

            ProdAgregarDTO productoEncontrado = productos.stream()
            .filter(p -> p.getCodigo().equals(codigo))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            ProductoCompra productoCompra = new ProductoCompra(
                productoEncontrado.getCodigo(),
                productoEncontrado.getNombre(),
                productoEncontrado.getPrecio()
            );
        });

        double total = compra.getDetalles().stream()
            .mapToDouble(d -> d.getProducto().getPrecio() * d.getCantidad())
            .sum();
        compra.setTotal(total);
        compra.setFecha(LocalDateTime.now());

        return compraRepo.save(compra);
    }

    //consumo de API productos - obtención de lista de productos
    public List<ProdAgregarDTO> obtenerProductosByCodigos(List<String> codigos){

        return productosWebClient.post()
        .uri("/lista")
        .bodyValue(codigos)
        .retrieve()
        .bodyToFlux(ProdAgregarDTO.class)
        .collectList()
        .block();
    }
}