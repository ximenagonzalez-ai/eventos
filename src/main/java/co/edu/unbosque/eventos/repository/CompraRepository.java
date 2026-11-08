package co.edu.unbosque.eventos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.eventos.model.Compra;

public interface CompraRepository extends JpaRepository<Compra, Long> {

    List<Compra> findByUsuarioIdOrderByFechaCompraDesc(Long usuarioId);
}