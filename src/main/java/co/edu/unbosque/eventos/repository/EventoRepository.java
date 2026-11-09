package co.edu.unbosque.eventos.repository;

import co.edu.unbosque.eventos.model.Evento;
import co.edu.unbosque.eventos.model.Evento.EstadoEvento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByCategoriaId(Long categoriaId);

    List<Evento> findByEstado(EstadoEvento estado);

    List<Evento> findByCategoriaIdAndEstado(Long categoriaId, EstadoEvento estado);

    List<Evento> findByLugarCiudadIgnoreCase(String ciudad);

    List<Evento> findByNombreContainingIgnoreCase(String nombre);

    List<Evento> findByFechaInicioBetween(LocalDateTime inicio, LocalDateTime fin);

    Optional<Evento> findByIdexterno(String idexterno);
}
