package com.example.compras.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCompraDTO {

    private String productoId;

    @NotBlank(message = "Cada producto debe tener nombre")
    private String nombre;

    @NotNull(message = "Cada producto debe tener cantidad")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @NotNull(message = "Cada producto debe tener precio")
    @PositiveOrZero(message = "El precio no puede ser negativo")
    private Double precio;
}