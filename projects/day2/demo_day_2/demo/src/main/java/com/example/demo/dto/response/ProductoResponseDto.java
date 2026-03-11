package com.example.demo.dto.response;

public record ProductoResponseDto(
        Long id,
        String nombre,
        Double precio,
        Integer stock,
        Long usuarioId,
        String usuarioNombre
) {
}
