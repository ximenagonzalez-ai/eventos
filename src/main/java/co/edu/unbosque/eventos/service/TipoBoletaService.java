package co.edu.unbosque.eventos.service;

import co.edu.unbosque.eventos.DTO.TipoBoletaDTO;
import co.edu.unbosque.eventos.model.Evento;
import co.edu.unbosque.eventos.model.TipoBoleta;
import co.edu.unbosque.eventos.repository.EventoRepository;
import co.edu.unbosque.eventos.repository.TipoBoletaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoBoletaService {

    private final TipoBoletaRepository tipoBoletaRepository;
    private final EventoRepository eventoRepository;

    public List<TipoBoletaDTO> getByEvento(Long eventoId) {
        return tipoBoletaRepository.findByEventoId(eventoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<TipoBoletaDTO> getById(Long id) {
        return tipoBoletaRepository.findById(id).map(this::toDTO);
    }

    /** Retorna 0=ok, 1=evento no existe */
    @Transactional
    public int crear(TipoBoletaDTO dto) {
        Optional<Evento> eventoOpt = eventoRepository.findById(dto.getEventoId());
        if (eventoOpt.isEmpty()) return 1;

        TipoBoleta tb = TipoBoleta.builder()
                .nombre(dto.getNombre())
                .precio(dto.getPrecio())
                .cantidadDisponible(dto.getCantidadDisponible())
                .descripcion(dto.getDescripcion())
                .evento(eventoOpt.get())
                .build();
        tipoBoletaRepository.save(tb);
        return 0;
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (!tipoBoletaRepository.existsById(id)) return false;
        tipoBoletaRepository.deleteById(id);
        return true;
    }

    private TipoBoletaDTO toDTO(TipoBoleta tb) {
        return TipoBoletaDTO.builder()
                .id(tb.getId())
                .nombre(tb.getNombre())
                .precio(tb.getPrecio())
                .cantidadDisponible(tb.getCantidadDisponible())
                .descripcion(tb.getDescripcion())
                .eventoId(tb.getEvento() != null ? tb.getEvento().getId() : null)
                .build();
    }
}
