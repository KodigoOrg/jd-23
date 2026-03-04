package com.example.demo.service;

import com.example.demo.domain.Producto;
import com.example.demo.dto.request.ProductoRequestDto;
import com.example.demo.dto.response.ProductoResponseDto;
import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public List<ProductoResponseDto> listarTodos() {
        return repository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    public ProductoResponseDto obtenerPorId(Long id) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto con id " + id + " no encontrado"));
        return toResponseDto(producto);
    }

    public ProductoResponseDto crear(ProductoRequestDto request) {
        Producto producto = new Producto(null, request.nombre(), request.precio(), request.stock());
        Producto saved = repository.save(producto);
        return toResponseDto(saved);
    }

    public ProductoResponseDto actualizar(Long id, ProductoRequestDto request) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Producto con id " + id + " no encontrado");
        }
        Producto updated = new Producto(id, request.nombre(), request.precio(), request.stock());
        Producto saved = repository.update(updated);
        return toResponseDto(saved);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Producto con id " + id + " no encontrado");
        }
        repository.deleteById(id);
    }

    private ProductoResponseDto toResponseDto(Producto producto) {
        return new ProductoResponseDto(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock()
        );
    }
}
