package co.edu.unbosque.eventos.repository;

import co.edu.unbosque.eventos.model.TipoBoleta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoBoletaRepository extends JpaRepository<TipoBoleta, Long> {

    List<TipoBoleta> findByEventoId(Long eventoId);
}
