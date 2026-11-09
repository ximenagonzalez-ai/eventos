package co.edu.unbosque.eventos.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.eventos.model.Itinerario;
import co.edu.unbosque.eventos.model.Usuario;
import co.edu.unbosque.eventos.repository.ItinerarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItinerarioService {

    private final ItinerarioRepository itinerarioRepository;
    private final AuditoriaService auditoriaService;

    @Transactional
    public Itinerario crear(Usuario usuario, String nombre) {
        Itinerario itinerario = Itinerario.builder()
                .usuario(usuario)
                .nombre(nombre)
                .build();
        itinerario = itinerarioRepository.save(itinerario);
        auditoriaService.registrar(usuario, "CREAR", "Itinerario", itinerario.getId(),
                "Itinerario creado: " + nombre);
        return itinerario;
    }

    public List<Itinerario> listarPorUsuario(Long usuarioId) {
        return itinerarioRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);
    }
}