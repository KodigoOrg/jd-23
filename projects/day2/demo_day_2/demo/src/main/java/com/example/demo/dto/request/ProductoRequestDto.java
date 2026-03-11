package com.example.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductoRequestDto(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotNull(message = "El precio es obligatorio")
        @Min(value = 0, message = "El precio debe ser mayor o igual a 0")
        Double precio,

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
        Integer stock,

        @NotNull(message = "El usuarioId es obligatorio")
        Long usuarioId
) {
}
