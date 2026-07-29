package com.cibertec.ms_credits.repository;

import com.cibertec.ms_credits.entity.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // @EntityGraph fuerza el fetch de "estado" en la misma consulta: JwtAuthenticationFilter
    // llama a este método fuera del alcance de la sesión OSIV (que Spring recién abre al
    // entrar al DispatcherServlet, después de los Filters de Spring Security), así que un
    // acceso LAZY normal a usuario.getEstado() ahí lanzaría LazyInitializationException.
    @EntityGraph(attributePaths = "estado")
    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);
}
