package com.example.compras.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.compras.client.UsuarioClient;
import com.example.compras.dto.CompraDTO;
import com.example.compras.dto.ItemCompraDTO;
import com.example.compras.exception.CompraNoEncontradaException;
import com.example.compras.exception.ServicioNoDisponibleException;
import com.example.compras.exception.UsuarioNoRegistradoException;
import com.example.compras.mapper.CompraMapper;
import com.example.compras.model.Compra;
import com.example.compras.model.DetalleCompra;
import com.example.compras.model.ProductoCompra;
import com.example.compras.repository.CompraRepository;

@ExtendWith(MockitoExtension.class)
class CompraServiceTest {

    private static final String CORREO = "cliente@correo.cl";

    @Mock
    private CompraRepository compraRepo;

    @Mock
    private CompraMapper compraMapper;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private CompraService compraService;

    /* ----------------------- helpers ----------------------- */

    // DTO valido con dos productos (los datos exactos no importan porque el mapper esta simulado)
    private CompraDTO crearDto() {
        CompraDTO dto = new CompraDTO();
        dto.setCorreo(CORREO);
        dto.setNombre("Ana");
        dto.setApellido("Perez");
        dto.setTelefono("912345678");
        dto.setCalle("Av. Siempre Viva 742");
        dto.setRegion("Metropolitana");
        dto.setComuna("Santiago");
        dto.setProductos(List.of(
            new ItemCompraDTO("P001", "Teclado", 2, 10000.0),
            new ItemCompraDTO("P002", "Mouse", 1, 5000.0)));
        return dto;
    }

    // Compra ya mapeada: 2 x 10000 + 1 x 5000 = 25000
    private Compra crearCompraMapeada() {
        Compra compra = new Compra();
        compra.setCorreo(CORREO);

        DetalleCompra d1 = new DetalleCompra();
        d1.setProducto(new ProductoCompra("P001", "Teclado", 10000.0));
        d1.setCantidad(2);

        DetalleCompra d2 = new DetalleCompra();
        d2.setProducto(new ProductoCompra("P002", "Mouse", 5000.0));
        d2.setCantidad(1);

        compra.setDetalles(new ArrayList<>(List.of(d1, d2)));
        return compra;
    }

    /* ----------------------- consultas ----------------------- */

    @Test
    @DisplayName("obtenerCompras devuelve la lista que entrega el repositorio")
    void obtenerCompras_retornaListaDelRepositorio() {
        List<Compra> compras = List.of(new Compra(), new Compra());
        when(compraRepo.findAllByOrderByFechaDesc()).thenReturn(compras);

        List<Compra> resultado = compraService.obtenerCompras();

        assertEquals(2, resultado.size());
        assertSame(compras, resultado);
        verify(compraRepo).findAllByOrderByFechaDesc();
    }

    @Test
    @DisplayName("obtenerCompraById devuelve la compra cuando existe")
    void obtenerCompraById_existe_retornaCompra() {
        Compra compra = new Compra();
        compra.setId(1L);
        when(compraRepo.findById(1L)).thenReturn(Optional.of(compra));

        Compra resultado = compraService.obtenerCompraById(1L);

        assertEquals(1L, resultado.getId());
    }

    @Test
    @DisplayName("obtenerCompraById lanza CompraNoEncontradaException cuando no existe")
    void obtenerCompraById_noExiste_lanzaExcepcion() {
        when(compraRepo.findById(99L)).thenReturn(Optional.empty());

        CompraNoEncontradaException ex = assertThrows(
            CompraNoEncontradaException.class,
            () -> compraService.obtenerCompraById(99L));

        assertEquals("Compra no encontrada", ex.getMessage());
    }

    @Test
    @DisplayName("obtenerComprasByUsuario devuelve las compras de ese usuario")
    void obtenerComprasByUsuario_conCompras_retornaLista() {
        Compra compra = new Compra();
        compra.setUsuarioId(7L);
        when(compraRepo.findByUsuarioIdOrderByFechaDesc(7L)).thenReturn(List.of(compra));

        List<Compra> resultado = compraService.obtenerComprasByUsuario(7L);

        assertEquals(1, resultado.size());
        assertEquals(7L, resultado.get(0).getUsuarioId());
    }

    @Test
    @DisplayName("obtenerComprasByUsuario devuelve lista vacia (no error) si el usuario no tiene compras")
    void obtenerComprasByUsuario_sinCompras_retornaListaVacia() {
        when(compraRepo.findByUsuarioIdOrderByFechaDesc(8L)).thenReturn(List.of());

        List<Compra> resultado = compraService.obtenerComprasByUsuario(8L);

        assertTrue(resultado.isEmpty());
    }

    /* ----------------------- registrarCompra: casos correctos ----------------------- */

    @Test
    @DisplayName("registrarCompra asigna el usuarioId del microservicio, pone la fecha y guarda la compra")
    void registrarCompra_valida_asignaUsuarioFechaYGuarda() {
        CompraDTO dto = crearDto();
        Compra compraMapeada = crearCompraMapeada();

        when(usuarioClient.obtenerIdPorCorreo(CORREO)).thenReturn(7L);
        when(compraMapper.toEntity(dto)).thenReturn(compraMapeada);
        when(compraRepo.save(any(Compra.class))).thenAnswer(inv -> inv.getArgument(0));

        LocalDateTime antes = LocalDateTime.now();
        Compra resultado = compraService.registrarCompra(dto);
        LocalDateTime despues = LocalDateTime.now();

        ArgumentCaptor<Compra> captor = ArgumentCaptor.forClass(Compra.class);
        verify(compraRepo).save(captor.capture());
        Compra guardada = captor.getValue();

        assertEquals(7L, guardada.getUsuarioId());
        assertNotNull(guardada.getFecha());
        assertFalse(guardada.getFecha().isBefore(antes));
        assertFalse(guardada.getFecha().isAfter(despues));
        assertSame(guardada, resultado);
    }

    @Test
    @DisplayName("registrarCompra calcula el total como suma de precio x cantidad de cada detalle")
    void registrarCompra_variosProductos_calculaTotal() {
        CompraDTO dto = crearDto();
        Compra compraMapeada = crearCompraMapeada();

        when(usuarioClient.obtenerIdPorCorreo(CORREO)).thenReturn(7L);
        when(compraMapper.toEntity(dto)).thenReturn(compraMapeada);
        when(compraRepo.save(any(Compra.class))).thenAnswer(inv -> inv.getArgument(0));

        Compra resultado = compraService.registrarCompra(dto);

        // 2 x 10000 + 1 x 5000 = 25000
        assertEquals(25000.0, resultado.getTotal(), 0.001);
    }

    /* ----------------------- registrarCompra: errores (no se guarda nada) ----------------------- */

    @Test
    @DisplayName("registrarCompra con correo sin cuenta lanza UsuarioNoRegistradoException y no guarda nada")
    void registrarCompra_usuarioNoRegistrado_lanzaExcepcionYNoGuarda() {
        CompraDTO dto = crearDto();
        when(usuarioClient.obtenerIdPorCorreo(CORREO)).thenThrow(new UsuarioNoRegistradoException());

        UsuarioNoRegistradoException ex = assertThrows(
            UsuarioNoRegistradoException.class,
            () -> compraService.registrarCompra(dto));

        assertEquals("Debes tener una cuenta para realizar compras", ex.getMessage());
        verify(compraRepo, never()).save(any(Compra.class));
        verifyNoInteractions(compraMapper);
    }

    @Test
    @DisplayName("registrarCompra con microservicio de usuarios caido lanza ServicioNoDisponibleException y no guarda nada")
    void registrarCompra_servicioUsuariosCaido_lanzaExcepcionYNoGuarda() {
        CompraDTO dto = crearDto();
        when(usuarioClient.obtenerIdPorCorreo(CORREO)).thenThrow(new ServicioNoDisponibleException());

        assertThrows(
            ServicioNoDisponibleException.class,
            () -> compraService.registrarCompra(dto));

        verify(compraRepo, never()).save(any(Compra.class));
        verifyNoInteractions(compraMapper);
    }
}