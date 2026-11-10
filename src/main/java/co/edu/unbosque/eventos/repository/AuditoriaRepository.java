package co.edu.unbosque.eventos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.eventos.model.Auditoria;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    List<Auditoria> findByUsuarioIdOrderByFechaRegistroDesc(Long usuarioId);

    List<Auditoria> findByEntidadAndEntidadId(String entidad, Long entidadId);
}