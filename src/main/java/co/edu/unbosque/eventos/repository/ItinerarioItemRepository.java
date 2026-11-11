package co.edu.unbosque.eventos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.eventos.model.ItinerarioItem;

public interface ItinerarioItemRepository extends JpaRepository<ItinerarioItem, Long> {

    List<ItinerarioItem> findByItinerarioId(Long itinerarioId);
}