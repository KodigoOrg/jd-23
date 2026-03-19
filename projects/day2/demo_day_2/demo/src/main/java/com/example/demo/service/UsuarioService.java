package com.example.demo.service;

import com.example.demo.domain.Producto;
import com.example.demo.domain.Usuario;
import com.example.demo.dto.request.UsuarioPatchRequestDto;
import com.example.demo.dto.request.UsuarioRequestDto;
import com.example.demo.dto.response.ProductoResponseDto;
import com.example.demo.dto.response.UsuarioConProductosResponseDto;
import com.example.demo.dto.response.UsuarioResponseDto;
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
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository repository;
    private final ProductoRepository productoRepository;

    public UsuarioService(UsuarioRepository repository, ProductoRepository productoRepository) {
        this.repository = repository;
        this.productoRepository = productoRepository;
    }

    public List<UsuarioResponseDto> listarTodos() {
        log.debug("Listando todos los usuarios");
        return repository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    public UsuarioResponseDto obtenerPorId(Long id) {
        log.debug("Buscando usuario con id={}", id);
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario con id={} no encontrado", id);
                    return new EntityNotFoundException("Usuario con id " + id + " no encontrado");
                });
        return toResponseDto(usuario);
    }

    public UsuarioResponseDto crear(UsuarioRequestDto request) {
        log.info("Creando usuario con email={}", request.email());
        Usuario usuario = new Usuario(null, request.nombre(), request.email());
        Usuario saved = repository.save(usuario);
        log.info("Usuario creado con id={}", saved.getId());
        return toResponseDto(saved);
    }

    public UsuarioResponseDto actualizar(Long id, UsuarioRequestDto request) {
        if (!repository.existsById(id)) {
            log.warn("Intento de actualizar usuario inexistente id={}", id);
            throw new EntityNotFoundException("Usuario con id " + id + " no encontrado");
        }
        Usuario updated = new Usuario(id, request.nombre(), request.email());
        Usuario saved = repository.save(updated);
        log.info("Usuario actualizado id={}", saved.getId());
        return toResponseDto(saved);
    }

    public UsuarioResponseDto actualizarParcial(Long id, UsuarioPatchRequestDto request) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Intento de actualizar parcialmente usuario inexistente id={}", id);
                    return new EntityNotFoundException("Usuario con id " + id + " no encontrado");
                });

        if (request.nombre() != null) {
            usuario.setNombre(request.nombre());
        }
        if (request.email() != null) {
            usuario.setEmail(request.email());
        }

        Usuario saved = repository.save(usuario);
        log.info("Usuario actualizado parcialmente id={}", saved.getId());
        return toResponseDto(saved);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            log.warn("Intento de eliminar usuario inexistente id={}", id);
            throw new EntityNotFoundException("Usuario con id " + id + " no encontrado");
        }
        repository.deleteById(id);
        log.info("Usuario eliminado id={}", id);
    }

    public List<UsuarioResponseDto> buscarPorNombre(String nombre) {
        log.debug("Buscando usuarios por nombre que contenga '{}'", nombre);
        return repository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::toResponseDto)
                .toList();
    }

    public UsuarioResponseDto buscarPorEmail(String email) {
        log.debug("Buscando usuario por email={}", email);
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Usuario con email={} no encontrado", email);
                    return new EntityNotFoundException("Usuario con email " + email + " no encontrado");
                });
        return toResponseDto(usuario);
    }

    public List<ProductoResponseDto> listarProductosPorUsuario(Long usuarioId) {
        if (!repository.existsById(usuarioId)) {
            log.warn("Intento de listar productos de usuario inexistente id={}", usuarioId);
            throw new EntityNotFoundException("Usuario con id " + usuarioId + " no encontrado");
        }
        log.info("Listando productos del usuario id={}", usuarioId);
        return productoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toProductoResponseDto)
                .toList();
    }

    public UsuarioConProductosResponseDto obtenerUsuarioConProductos(Long usuarioId) {
        Usuario usuario = repository.findByIdWithProductos(usuarioId)
                .orElseThrow(() -> {
                    log.warn("Usuario con id={} no encontrado (con productos)", usuarioId);
                    return new EntityNotFoundException("Usuario con id " + usuarioId + " no encontrado");
                });

        List<ProductoResponseDto> productos = usuario.getProductos().stream()
                .map(this::toProductoResponseDto)
                .toList();

        log.info("Usuario con productos recuperado id={} productos={}", usuario.getId(), productos.size());
        return new UsuarioConProductosResponseDto(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                productos
        );
    }

    private UsuarioResponseDto toResponseDto(Usuario usuario) {
        return new UsuarioResponseDto(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail()
        );
    }

    private ProductoResponseDto toProductoResponseDto(Producto producto) {
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
