package co.edu.unbosque.eventos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.edu.unbosque.eventos.model.Boleta;

public interface BoletaRepository extends JpaRepository<Boleta, Long> {

    List<Boleta> findByCompraId(Long compraId);

    @Query("SELECT b.tipoBoleta.evento.nombre, COUNT(b), SUM(b.precioPagado) FROM Boleta b GROUP BY b.tipoBoleta.evento.nombre")
    List<Object[]> ventasPorEvento();
}