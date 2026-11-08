package co.edu.unbosque.eventos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.eventos.model.Usuario;

import java.util.Optional;
 
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
 
    Optional<Usuario> findByEmail(String email);
 
    boolean existsByEmail(String email);
}
