package co.edu.unbosque.eventos.service;

import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.unbosque.eventos.model.Auditoria;
import co.edu.unbosque.eventos.model.Usuario;
import co.edu.unbosque.eventos.repository.AuditoriaRepository;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    public Auditoria registrar(Usuario usuario, String accion, String entidad, Long entidadId, String detalle) {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario(usuario);
        auditoria.setAccion(accion);
        auditoria.setEntidad(entidad);
        auditoria.setEntidadId(entidadId);
        auditoria.setDetalle(detalle);
        return auditoriaRepository.save(auditoria);
    }

    public List<Auditoria> listarPorUsuario(Long usuarioId) {
        return auditoriaRepository.findByUsuarioIdOrderByFechaRegistroDesc(usuarioId);
    }
}