package com.example.demo.service;

import com.example.demo.domain.Producto;
import com.example.demo.domain.Usuario;
import com.example.demo.dto.request.ProductoPatchRequestDto;
import com.example.demo.dto.request.ProductoRequestDto;
import com.example.demo.dto.response.ProductoResponseDto;
import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository repository;
    private final UsuarioRepository usuarioRepository;

    public ProductoService(ProductoRepository repository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
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
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuario con id " + request.usuarioId() + " no encontrado"));
        Producto producto = new Producto(null, request.nombre(), request.precio(), request.stock(), usuario);
        Producto saved = repository.save(producto);
        return toResponseDto(saved);
    }

    public ProductoResponseDto actualizar(Long id, ProductoRequestDto request) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Producto con id " + id + " no encontrado");
        }
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuario con id " + request.usuarioId() + " no encontrado"));
        Producto updated = new Producto(id, request.nombre(), request.precio(), request.stock(), usuario);
        Producto saved = repository.save(updated);
        return toResponseDto(saved);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Producto con id " + id + " no encontrado");
        }
        repository.deleteById(id);
    }

    public List<ProductoResponseDto> buscarPorNombre(String nombre) {
        return repository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::toResponseDto)
                .toList();
    }

    public Integer obtenerStock(Long id) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto con id " + id + " no encontrado"));
        return producto.getStock();
    }

    public ProductoResponseDto actualizarParcial(Long id, ProductoPatchRequestDto request) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto con id " + id + " no encontrado"));

        if (request.nombre() != null) {
            producto.setNombre(request.nombre());
        }
        if (request.precio() != null) {
            producto.setPrecio(request.precio());
        }
        if (request.stock() != null) {
            producto.setStock(request.stock());
        }
        if (request.usuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(request.usuarioId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Usuario con id " + request.usuarioId() + " no encontrado"));
            producto.setUsuario(usuario);
        }

        Producto saved = repository.save(producto);
        return toResponseDto(saved);
    }

    private ProductoResponseDto toResponseDto(Producto producto) {
        return new ProductoResponseDto(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getUsuario().getId(),
                producto.getUsuario().getNombre()
        );
    }
}
