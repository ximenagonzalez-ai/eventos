
package co.edu.unbosque.eventos.repository;

import co.edu.unbosque.eventos.model.Evento;
import co.edu.unbosque.eventos.model.Evento.EstadoEvento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    @EntityGraph(attributePaths = {
        "categoria",
        "lugar",
        "tiposBoleta"
    })
    List<Evento> findAllBy();

    @EntityGraph(attributePaths = {
        "categoria",
        "lugar",
        "tiposBoleta"
    })
    List<Evento> findByCategoriaId(Long categoriaId);

    @EntityGraph(attributePaths = {
        "categoria",
        "lugar",
        "tiposBoleta"
    })
    List<Evento> findByEstado(EstadoEvento estado);

    @EntityGraph(attributePaths = {
        "categoria",
        "lugar",
        "tiposBoleta"
    })
    List<Evento> findByCategoriaIdAndEstado(
        Long categoriaId,
        EstadoEvento estado
    );

    @EntityGraph(attributePaths = {
        "categoria",
        "lugar",
        "tiposBoleta"
    })
    List<Evento> findByLugarCiudadIgnoreCase(String ciudad);

    @EntityGraph(attributePaths = {
        "categoria",
        "lugar",
        "tiposBoleta"
    })
    List<Evento> findByNombreContainingIgnoreCase(String nombre);

    @EntityGraph(attributePaths = {
        "categoria",
        "lugar",
        "tiposBoleta"
    })
    List<Evento> findByFechaInicioBetween(
        LocalDateTime inicio,
        LocalDateTime fin
    );

    Optional<Evento> findByIdexterno(String idexterno);
}
