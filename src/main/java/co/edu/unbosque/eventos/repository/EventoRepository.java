package co.edu.unbosque.eventos.repository;

import co.edu.unbosque.eventos.model.Evento;
import co.edu.unbosque.eventos.model.Evento.EstadoEvento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de eventos.
 * Todas las consultas de lectura usan JOIN FETCH para traer la categoria,
 * el lugar y los tipos de boleta en la misma consulta. Asi no ocurre
 * LazyInitializationException al armar los DTO fuera de la sesion de Hibernate.
 */
public interface EventoRepository extends JpaRepository<Evento, Long> {

    String BASE = "select distinct e from Evento e "
            + "join fetch e.categoria c "
            + "join fetch e.lugar l "
            + "left join fetch e.tiposBoleta ";

    @Query(BASE)
    List<Evento> findAllBy();

    @Query(BASE + "where e.id = :id")
    Optional<Evento> findDetalleById(@Param("id") Long id);

    @Query(BASE + "where c.id = :categoriaId")
    List<Evento> findByCategoriaId(@Param("categoriaId") Long categoriaId);

    @Query(BASE + "where e.estado = :estado")
    List<Evento> findByEstado(@Param("estado") EstadoEvento estado);

    @Query(BASE + "where c.id = :categoriaId and e.estado = :estado")
    List<Evento> findByCategoriaIdAndEstado(@Param("categoriaId") Long categoriaId,
                                            @Param("estado") EstadoEvento estado);

    @Query(BASE + "where lower(l.ciudad) = lower(:ciudad)")
    List<Evento> findByLugarCiudadIgnoreCase(@Param("ciudad") String ciudad);

    @Query(BASE + "where lower(e.nombre) like lower(concat('%', :nombre, '%'))")
    List<Evento> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);

    @Query(BASE + "where e.fechaInicio between :inicio and :fin")
    List<Evento> findByFechaInicioBetween(@Param("inicio") LocalDateTime inicio,
                                          @Param("fin") LocalDateTime fin);

    Optional<Evento> findByIdexterno(String idexterno);
}