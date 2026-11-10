package co.edu.unbosque.eventos.repository;

import co.edu.unbosque.eventos.model.Lugar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LugarRepository extends JpaRepository<Lugar, Long> {

    List<Lugar> findByCiudadIgnoreCase(String ciudad);

    List<Lugar> findByNombreContainingIgnoreCase(String nombre);
}
