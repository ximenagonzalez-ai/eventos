package co.edu.unbosque.eventos.service;

import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.unbosque.eventos.model.Auditoria;
import co.edu.unbosque.eventos.model.Usuario;
import co.edu.unbosque.eventos.repository.AuditoriaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public Auditoria registrar(Usuario usuario, String accion, String entidad, Long entidadId, String detalle) {
        Auditoria auditoria = Auditoria.builder()
                .usuario(usuario)
                .accion(accion)
                .entidad(entidad)
                .entidadId(entidadId)
                .detalle(detalle)
                .build();
        return auditoriaRepository.save(auditoria);
    }

    public List<Auditoria> listarPorUsuario(Long usuarioId) {
        return auditoriaRepository.findByUsuarioIdOrderByFechaRegistroDesc(usuarioId);
    }
}