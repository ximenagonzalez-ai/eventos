package co.edu.unbosque.eventos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.eventos.model.TipoToken;
import co.edu.unbosque.eventos.model.TokenUsuario;
import co.edu.unbosque.eventos.model.Usuario;

import java.util.List;
import java.util.Optional;
 
public interface TokenUsuarioRepository extends JpaRepository<TokenUsuario, Long> {
 
    Optional<TokenUsuario> findByToken(String token);
 
    List<TokenUsuario> findByUsuarioAndTipoAndUsadoFalse(Usuario usuario, TipoToken tipo);
}
