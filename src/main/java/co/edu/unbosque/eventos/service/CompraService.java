package co.edu.unbosque.eventos.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.eventos.model.Compra;
import co.edu.unbosque.eventos.model.EstadoCompra;
import co.edu.unbosque.eventos.model.Usuario;
import co.edu.unbosque.eventos.repository.CompraRepository;

@Service
public class CompraService {

    private final CompraRepository compraRepository;
    private final AuditoriaService auditoriaService;

    public CompraService(CompraRepository compraRepository, AuditoriaService auditoriaService) {
        this.compraRepository = compraRepository;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public Compra crear(Usuario usuario, BigDecimal total) {
        Compra compra = new Compra();
        compra.setUsuario(usuario);
        compra.setTotal(total);
        compra = compraRepository.save(compra);
        auditoriaService.registrar(usuario, "CREAR", "Compra", compra.getId(),
                "Compra creada por un total de " + total);
        return compra;
    }

    @Transactional
    public Compra completar(Long compraId) {
        Compra compra = compraRepository.findById(compraId)
                .orElseThrow(() -> new IllegalArgumentException("Compra no encontrada: " + compraId));
        compra.setEstado(EstadoCompra.COMPLETADA);
        compra = compraRepository.save(compra);
        auditoriaService.registrar(compra.getUsuario(), "COMPLETAR", "Compra", compra.getId(),
                "Compra marcada como completada");
        return compra;
    }

    public List<Compra> listarPorUsuario(Long usuarioId) {
        return compraRepository.findByUsuarioIdOrderByFechaCompraDesc(usuarioId);
    }
}