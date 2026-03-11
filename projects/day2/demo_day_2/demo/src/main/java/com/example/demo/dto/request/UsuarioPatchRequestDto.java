package com.example.demo.dto.request;

import jakarta.validation.constraints.Email;

public record UsuarioPatchRequestDto(
        String nombre,

        @Email(message = "El email debe tener un formato valido")
        String email
) {
}
