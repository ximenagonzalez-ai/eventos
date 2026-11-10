package co.edu.unbosque.eventos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.eventos.model.Itinerario;

public interface ItinerarioRepository extends JpaRepository<Itinerario, Long> {

    List<Itinerario> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
}