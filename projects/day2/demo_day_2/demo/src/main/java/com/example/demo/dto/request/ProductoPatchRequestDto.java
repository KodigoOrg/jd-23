package com.example.demo.dto.request;

import jakarta.validation.constraints.Min;

public record ProductoPatchRequestDto(
        String nombre,

        @Min(value = 0, message = "El precio debe ser mayor o igual a 0")
        Double precio,

        @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
        Integer stock,

        Long usuarioId
) {
}
