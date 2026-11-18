package co.edu.unbosque.eventos.repository;

import co.edu.unbosque.eventos.model.PreferenciaUsuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferenciaUsuarioRepository extends JpaRepository<PreferenciaUsuario, Long> {

    Optional<PreferenciaUsuario> findByUsuarioId(Long usuarioId);
}
