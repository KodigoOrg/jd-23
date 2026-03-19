package com.example.demo.controller;

import com.example.demo.dto.request.UsuarioPatchRequestDto;
import com.example.demo.dto.request.UsuarioRequestDto;
import com.example.demo.dto.response.ProductoResponseDto;
import com.example.demo.dto.response.UsuarioConProductosResponseDto;
import com.example.demo.dto.response.UsuarioResponseDto;
import com.example.demo.exception.ErrorResponse;
import com.example.demo.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "CRUD de usuarios")
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todos los usuarios")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida")
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDto>> listarTodos() {
        log.debug("GET /api/usuarios - listar todos");
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Obtener usuario por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> obtenerPorId(@PathVariable Long id) {
        log.debug("GET /api/usuarios/{} - obtener por id", id);
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(summary = "Crear un nuevo usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<UsuarioResponseDto> crear(@Valid @RequestBody UsuarioRequestDto request) {
        log.info("POST /api/usuarios - crear usuario {}", request.email());
        UsuarioResponseDto created = service.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Actualizar un usuario existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> actualizar(@PathVariable Long id,
                                                         @Valid @RequestBody UsuarioRequestDto request) {
        log.info("PUT /api/usuarios/{} - actualizar usuario", id);
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @Operation(summary = "Actualización parcial de un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado parcialmente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> actualizarParcial(@PathVariable Long id,
                                                                @Valid @RequestBody UsuarioPatchRequestDto request) {
        log.info("PATCH /api/usuarios/{} - actualización parcial", id);
        return ResponseEntity.ok(service.actualizarParcial(id, request));
    }

    @Operation(summary = "Eliminar un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/usuarios/{} - eliminar usuario", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar usuarios por nombre")
    @ApiResponse(responseCode = "200", description = "Resultados obtenidos")
    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioResponseDto>> buscarPorNombre(@RequestParam String nombre) {
        log.debug("GET /api/usuarios/buscar - nombre={}", nombre);
        return ResponseEntity.ok(service.buscarPorNombre(nombre));
    }

    @Operation(summary = "Buscar usuario por email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/buscar-por-email")
    public ResponseEntity<UsuarioResponseDto> buscarPorEmail(@RequestParam String email) {
        log.debug("GET /api/usuarios/buscar-por-email - email={}", email);
        return ResponseEntity.ok(service.buscarPorEmail(email));
    }

    @Operation(summary = "Listar productos de un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Productos del usuario obtenidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}/productos")
    public ResponseEntity<List<ProductoResponseDto>> listarProductosPorUsuario(@PathVariable Long id) {
        log.debug("GET /api/usuarios/{}/productos - listar productos", id);
        return ResponseEntity.ok(service.listarProductosPorUsuario(id));
    }

    @Operation(summary = "Obtener usuario con productos (join fetch)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario con productos obtenido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}/con-productos")
    public ResponseEntity<UsuarioConProductosResponseDto> obtenerUsuarioConProductos(@PathVariable Long id) {
        log.debug("GET /api/usuarios/{}/con-productos - obtener usuario con productos", id);
        return ResponseEntity.ok(service.obtenerUsuarioConProductos(id));
    }
}
