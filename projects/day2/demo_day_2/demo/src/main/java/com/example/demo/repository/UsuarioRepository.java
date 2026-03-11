package com.example.demo.repository;

import com.example.demo.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    Optional<Usuario> findByEmail(String email);

    @Query("select u from Usuario u left join fetch u.productos where u.id = :id")
    Optional<Usuario> findByIdWithProductos(@Param("id") Long id);
}
