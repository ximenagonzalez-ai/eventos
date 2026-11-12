package co.edu.unbosque.eventos.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.eventos.DTO.ItinerarioDTO;
import co.edu.unbosque.eventos.model.Evento;
import co.edu.unbosque.eventos.model.Itinerario;
import co.edu.unbosque.eventos.model.ItinerarioItem;
import co.edu.unbosque.eventos.model.Usuario;
import co.edu.unbosque.eventos.repository.EventoRepository;
import co.edu.unbosque.eventos.repository.ItinerarioItemRepository;
import co.edu.unbosque.eventos.repository.ItinerarioRepository;
import co.edu.unbosque.eventos.repository.UsuarioRepository;

@Service
public class ItinerarioService {

    private final ItinerarioRepository itinerarioRepository;
    private final ItinerarioItemRepository itinerarioItemRepository;
    private final UsuarioRepository usuarioRepository;
    private final EventoRepository eventoRepository;
    private final AuditoriaService auditoriaService;

    public ItinerarioService(ItinerarioRepository itinerarioRepository,
            ItinerarioItemRepository itinerarioItemRepository, UsuarioRepository usuarioRepository,
            EventoRepository eventoRepository, AuditoriaService auditoriaService) {
        this.itinerarioRepository = itinerarioRepository;
        this.itinerarioItemRepository = itinerarioItemRepository;
        this.usuarioRepository = usuarioRepository;
        this.eventoRepository = eventoRepository;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public ItinerarioDTO crear(Long usuarioId, String nombre) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuarioId));
        Itinerario itinerario = new Itinerario();
        itinerario.setUsuario(usuario);
        itinerario.setNombre(nombre);
        itinerario = itinerarioRepository.save(itinerario);
        auditoriaService.registrar(usuario, "CREAR", "Itinerario", itinerario.getId(),
                "Itinerario creado: " + nombre);
        return convertir(itinerario);
    }

    @Transactional
    public ItinerarioDTO agregarEvento(Long itinerarioId, Long eventoId) {
        Itinerario itinerario = itinerarioRepository.findById(itinerarioId)
                .orElseThrow(() -> new IllegalArgumentException("Itinerario no encontrado: " + itinerarioId));
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado: " + eventoId));

        ItinerarioItem item = new ItinerarioItem();
        item.setItinerario(itinerario);
        item.setEvento(evento);
        itinerarioItemRepository.save(item);

        auditoriaService.registrar(itinerario.getUsuario(), "AGREGAR_EVENTO", "Itinerario", itinerarioId,
                "Evento agregado: " + evento.getNombre());
        return convertir(itinerario);
    }

    @Transactional(readOnly = true)
    public ItinerarioDTO obtener(Long itinerarioId) {
        Itinerario itinerario = itinerarioRepository.findById(itinerarioId)
                .orElseThrow(() -> new IllegalArgumentException("Itinerario no encontrado: " + itinerarioId));
        return convertir(itinerario);
    }

    @Transactional(readOnly = true)
    public List<ItinerarioDTO> listarPorUsuario(Long usuarioId) {
        List<ItinerarioDTO> respuesta = new ArrayList<>();
        for (Itinerario itinerario : itinerarioRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId)) {
            respuesta.add(convertir(itinerario));
        }
        return respuesta;
    }

    private ItinerarioDTO convertir(Itinerario itinerario) {
        List<String> nombres = new ArrayList<>();
        for (ItinerarioItem item : itinerarioItemRepository.findByItinerarioId(itinerario.getId())) {
            nombres.add(item.getEvento().getNombre());
        }
        return ItinerarioDTO.convertirAItinerarioDTO(itinerario, nombres);
    }
}