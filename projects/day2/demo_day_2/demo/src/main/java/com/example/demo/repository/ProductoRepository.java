package com.example.demo.repository;

import com.example.demo.domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad {@link Producto}.
 * <p>
 * Spring Data JPA genera automaticamente la implementacion en tiempo de ejecucion.
 * Metodos heredados incluyen: {@code save()}, {@code findById()}, {@code findAll()},
 * {@code existsById()}, {@code deleteById()}, entre otros.
 * <p>
 * Para agregar queries personalizados, se pueden definir metodos siguiendo
 * la convencion de nombres de Spring Data (ej: {@code findByNombre(String nombre)})
 * o usar {@code @Query} con JPQL/SQL nativo.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByUsuarioId(Long usuarioId);

    List<Producto> findByNombreContainingIgnoreCaseAndUsuarioId(String nombre, Long usuarioId);
}
