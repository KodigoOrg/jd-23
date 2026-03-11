package com.example.demo.controller;

import com.example.demo.dto.request.ProductoPatchRequestDto;
import com.example.demo.dto.request.ProductoRequestDto;
import com.example.demo.dto.response.ProductoResponseDto;
import com.example.demo.exception.ErrorResponse;
import com.example.demo.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "CRUD de productos en memoria")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todos los productos")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida")
    @GetMapping
    public ResponseEntity<List<ProductoResponseDto>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Obtener producto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(summary = "Crear un nuevo producto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ProductoResponseDto> crear(@Valid @RequestBody ProductoRequestDto request) {
        ProductoResponseDto created = service.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Actualizar un producto existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> actualizar(@PathVariable Long id,
                                                          @Valid @RequestBody ProductoRequestDto request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @Operation(summary = "Eliminar un producto")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // =====================================================================
    // DEMOS: Extracción de datos — @PathVariable, @RequestParam, @RequestBody
    // =====================================================================

    @Operation(summary = "Obtener stock de un producto por ID",
            description = "Ejemplo de @PathVariable: extrae el {id} de la URI /api/productos/{id}/stock")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock obtenido"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}/stock")
    public ResponseEntity<Integer> obtenerStock(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerStock(id));
    }

    @Operation(summary = "Buscar productos por nombre",
            description = "Ejemplo de @RequestParam: extrae 'nombre' del query string ?nombre=mesa")
    @ApiResponse(responseCode = "200", description = "Resultados de búsqueda obtenidos")
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoResponseDto>> buscarPorNombre(
            @RequestParam String nombre) {
        return ResponseEntity.ok(service.buscarPorNombre(nombre));
    }

    @Operation(summary = "Actualización parcial de un producto",
            description = "Ejemplo de @RequestBody: extrae el JSON del body HTTP para actualización parcial (PATCH)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado parcialmente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> actualizarParcial(
            @PathVariable Long id,
            @Valid @RequestBody ProductoPatchRequestDto request) {
        return ResponseEntity.ok(service.actualizarParcial(id, request));
    }
}
