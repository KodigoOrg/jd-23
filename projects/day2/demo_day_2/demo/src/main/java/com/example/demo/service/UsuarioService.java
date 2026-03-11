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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository repository;
    private final ProductoRepository productoRepository;

    public UsuarioService(UsuarioRepository repository, ProductoRepository productoRepository) {
        this.repository = repository;
        this.productoRepository = productoRepository;
    }

    public List<UsuarioResponseDto> listarTodos() {
        return repository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    public UsuarioResponseDto obtenerPorId(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario con id " + id + " no encontrado"));
        return toResponseDto(usuario);
    }

    public UsuarioResponseDto crear(UsuarioRequestDto request) {
        Usuario usuario = new Usuario(null, request.nombre(), request.email());
        Usuario saved = repository.save(usuario);
        return toResponseDto(saved);
    }

    public UsuarioResponseDto actualizar(Long id, UsuarioRequestDto request) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Usuario con id " + id + " no encontrado");
        }
        Usuario updated = new Usuario(id, request.nombre(), request.email());
        Usuario saved = repository.save(updated);
        return toResponseDto(saved);
    }

    public UsuarioResponseDto actualizarParcial(Long id, UsuarioPatchRequestDto request) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario con id " + id + " no encontrado"));

        if (request.nombre() != null) {
            usuario.setNombre(request.nombre());
        }
        if (request.email() != null) {
            usuario.setEmail(request.email());
        }

        Usuario saved = repository.save(usuario);
        return toResponseDto(saved);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Usuario con id " + id + " no encontrado");
        }
        repository.deleteById(id);
    }

    public List<UsuarioResponseDto> buscarPorNombre(String nombre) {
        return repository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::toResponseDto)
                .toList();
    }

    public UsuarioResponseDto buscarPorEmail(String email) {
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario con email " + email + " no encontrado"));
        return toResponseDto(usuario);
    }

    public List<ProductoResponseDto> listarProductosPorUsuario(Long usuarioId) {
        if (!repository.existsById(usuarioId)) {
            throw new EntityNotFoundException("Usuario con id " + usuarioId + " no encontrado");
        }
        return productoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toProductoResponseDto)
                .toList();
    }

    public UsuarioConProductosResponseDto obtenerUsuarioConProductos(Long usuarioId) {
        Usuario usuario = repository.findByIdWithProductos(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario con id " + usuarioId + " no encontrado"));

        List<ProductoResponseDto> productos = usuario.getProductos().stream()
                .map(this::toProductoResponseDto)
                .toList();

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
