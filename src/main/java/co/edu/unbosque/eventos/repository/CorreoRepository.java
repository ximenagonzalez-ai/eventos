package co.edu.unbosque.eventos.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.eventos.model.*;
public interface CorreoRepository extends JpaRepository<Correo, Long> {

	List<Correo> findByUsuarioIdOrderByFechaEnvioDesc(Long usuarioId);
}
