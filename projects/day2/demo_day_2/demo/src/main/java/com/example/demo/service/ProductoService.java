package com.example.demo.service;

import com.example.demo.domain.Producto;
import com.example.demo.domain.Usuario;
import com.example.demo.dto.request.ProductoPatchRequestDto;
import com.example.demo.dto.request.ProductoRequestDto;
import com.example.demo.dto.response.ProductoResponseDto;
import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    private final ProductoRepository repository;
    private final UsuarioRepository usuarioRepository;

    public ProductoService(ProductoRepository repository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<ProductoResponseDto> listarTodos() {
        log.debug("Listando todos los productos");
        return repository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    public ProductoResponseDto obtenerPorId(Long id) {
        log.debug("Buscando producto con id={}", id);
        Producto producto = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Producto con id={} no encontrado", id);
                    return new EntityNotFoundException("Producto con id " + id + " no encontrado");
                });
        return toResponseDto(producto);
    }

    public ProductoResponseDto crear(ProductoRequestDto request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> {
                    log.warn("Usuario con id={} no encontrado al crear producto", request.usuarioId());
                    return new EntityNotFoundException(
                            "Usuario con id " + request.usuarioId() + " no encontrado");
                });
        Producto producto = new Producto(null, request.nombre(), request.precio(), request.stock(), usuario);
        Producto saved = repository.save(producto);
        log.info("Producto creado id={} para usuario={}", saved.getId(), usuario.getId());
        return toResponseDto(saved);
    }

    public ProductoResponseDto actualizar(Long id, ProductoRequestDto request) {
        if (!repository.existsById(id)) {
            log.warn("Intento de actualizar producto inexistente id={}", id);
            throw new EntityNotFoundException("Producto con id " + id + " no encontrado");
        }
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> {
                    log.warn("Usuario con id={} no encontrado al actualizar producto id={}", request.usuarioId(), id);
                    return new EntityNotFoundException(
                            "Usuario con id " + request.usuarioId() + " no encontrado");
                });
        Producto updated = new Producto(id, request.nombre(), request.precio(), request.stock(), usuario);
        Producto saved = repository.save(updated);
        log.info("Producto actualizado id={} para usuario={}", saved.getId(), usuario.getId());
        return toResponseDto(saved);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            log.warn("Intento de eliminar producto inexistente id={}", id);
            throw new EntityNotFoundException("Producto con id " + id + " no encontrado");
        }
        repository.deleteById(id);
        log.info("Producto eliminado id={}", id);
    }

    public List<ProductoResponseDto> buscarPorNombre(String nombre) {
        log.debug("Buscando productos por nombre que contenga '{}'", nombre);
        return repository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::toResponseDto)
                .toList();
    }

    public Integer obtenerStock(Long id) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Producto con id={} no encontrado al consultar stock", id);
                    return new EntityNotFoundException("Producto con id " + id + " no encontrado");
                });
        log.debug("Stock consultado para producto id={}", id);
        return producto.getStock();
    }

    public ProductoResponseDto actualizarParcial(Long id, ProductoPatchRequestDto request) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Intento de actualización parcial de producto inexistente id={}", id);
                    return new EntityNotFoundException("Producto con id " + id + " no encontrado");
                });

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
                    .orElseThrow(() -> {
                        log.warn("Usuario con id={} no encontrado al actualizar parcialmente producto id={}",
                                request.usuarioId(), id);
                        return new EntityNotFoundException(
                                "Usuario con id " + request.usuarioId() + " no encontrado");
                    });
            producto.setUsuario(usuario);
        }

        Producto saved = repository.save(producto);
        log.info("Producto actualizado parcialmente id={}", saved.getId());
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
