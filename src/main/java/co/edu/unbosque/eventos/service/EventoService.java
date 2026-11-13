package co.edu.unbosque.eventos.service;

import co.edu.unbosque.eventos.dto.EventoDTO;
import co.edu.unbosque.eventos.dto.LugarDTO;
import co.edu.unbosque.eventos.dto.TipoBoletaDTO;
import co.edu.unbosque.eventos.model.Categoria;
import co.edu.unbosque.eventos.model.Evento;
import co.edu.unbosque.eventos.model.Evento.EstadoEvento;
import co.edu.unbosque.eventos.model.Evento.FuenteEvento;
import co.edu.unbosque.eventos.model.Lugar;
import co.edu.unbosque.eventos.model.TipoBoleta;
import co.edu.unbosque.eventos.repository.CategoriaRepository;
import co.edu.unbosque.eventos.repository.EventoRepository;
import co.edu.unbosque.eventos.repository.LugarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final CategoriaRepository categoriaRepository;
    private final LugarRepository lugarRepository;

    public long count() {
        return eventoRepository.count();
    }

    public boolean exist(Long id) {
        return eventoRepository.existsById(id);
    }

    /** Retorna 0=ok, 1=categoría no encontrada, 2=lugar no encontrado */
    @Transactional
    public int crear(EventoDTO dto) {
        Optional<Categoria> cat = categoriaRepository.findById(dto.getCategoriaId());
        if (cat.isEmpty()) return 1;

        Optional<Lugar> lugar = lugarRepository.findById(dto.getLugar().getId());
        if (lugar.isEmpty()) return 2;

        Evento evento = Evento.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .imagenUrl(dto.getImagenUrl())
                .estado(dto.getEstado() != null ? dto.getEstado() : EstadoEvento.PROXIMO)
                .fuente(FuenteEvento.LOCAL)
                .categoria(cat.get())
                .lugar(lugar.get())
                .build();

        eventoRepository.save(evento);
        return 0;
    }

    public List<EventoDTO> getAll() {
        return eventoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<EventoDTO> getById(Long id) {
        return eventoRepository.findById(id).map(this::toDTO);
    }

    public List<EventoDTO> getByCategoria(Long categoriaId) {
        return eventoRepository.findByCategoriaId(categoriaId).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public List<EventoDTO> getByEstado(EstadoEvento estado) {
        return eventoRepository.findByEstado(estado).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public List<EventoDTO> getByCiudad(String ciudad) {
        return eventoRepository.findByLugarCiudadIgnoreCase(ciudad).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public List<EventoDTO> buscarPorNombre(String nombre) {
        return eventoRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public List<EventoDTO> getByRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return eventoRepository.findByFechaInicioBetween(inicio, fin).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    /** Retorna 0=ok, 1=cat no existe, 2=lugar no existe, 3=evento no existe */
    @Transactional
    public int actualizar(Long id, EventoDTO dto) {
        Optional<Evento> found = eventoRepository.findById(id);
        if (found.isEmpty()) return 3;

        Evento evento = found.get();

        if (dto.getCategoriaId() != null) {
            Optional<Categoria> cat = categoriaRepository.findById(dto.getCategoriaId());
            if (cat.isEmpty()) return 1;
            evento.setCategoria(cat.get());
        }

        if (dto.getLugar() != null && dto.getLugar().getId() != null) {
            Optional<Lugar> lugar = lugarRepository.findById(dto.getLugar().getId());
            if (lugar.isEmpty()) return 2;
            evento.setLugar(lugar.get());
        }

        if (dto.getNombre() != null) evento.setNombre(dto.getNombre());
        if (dto.getDescripcion() != null) evento.setDescripcion(dto.getDescripcion());
        if (dto.getFechaInicio() != null) evento.setFechaInicio(dto.getFechaInicio());
        if (dto.getFechaFin() != null) evento.setFechaFin(dto.getFechaFin());
        if (dto.getImagenUrl() != null) evento.setImagenUrl(dto.getImagenUrl());
        if (dto.getEstado() != null) evento.setEstado(dto.getEstado());

        eventoRepository.save(evento);
        return 0;
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (!eventoRepository.existsById(id)) return false;
        eventoRepository.deleteById(id);
        return true;
    }

    /**
     * Guarda eventos externos (Ticketmaster), evitando duplicados por idexterno.
     * @return cantidad de eventos nuevos guardados
     */
    @Transactional
    public int sincronizarDesdeApiExterna(List<EventoDTO> externos) {
        int nuevos = 0;
        for (EventoDTO dto : externos) {
            try {
                // Evitar duplicados
                if (dto.getIdexterno() != null
                        && eventoRepository.findByIdexterno(dto.getIdexterno()).isPresent()) {
                    continue;
                }

                if (dto.getCategoriaId() == null) continue;
                Optional<Categoria> cat = categoriaRepository.findById(dto.getCategoriaId());
                if (cat.isEmpty()) continue;

                if (dto.getLugar() == null) continue;

                Lugar lugar;
                if (dto.getLugar().getId() != null) {
                    Optional<Lugar> l = lugarRepository.findById(dto.getLugar().getId());
                    if (l.isEmpty()) continue;
                    lugar = l.get();
                } else {
                    // Buscar por nombre o crear
                    List<Lugar> existentes = lugarRepository.findByNombreContainingIgnoreCase(
                            dto.getLugar().getNombre() != null ? dto.getLugar().getNombre() : "");
                    if (!existentes.isEmpty()) {
                        lugar = existentes.get(0);
                    } else {
                        Lugar nuevo = Lugar.builder()
                                .nombre(dto.getLugar().getNombre() != null ? dto.getLugar().getNombre() : "Sin nombre")
                                .direccion(dto.getLugar().getDireccion() != null ? dto.getLugar().getDireccion() : "Sin dirección")
                                .ciudad(dto.getLugar().getCiudad() != null ? dto.getLugar().getCiudad() : "Sin ciudad")
                                .latitud(dto.getLugar().getLatitud() != null ? dto.getLugar().getLatitud() : 0.0)
                                .longitud(dto.getLugar().getLongitud() != null ? dto.getLugar().getLongitud() : 0.0)
                                .descripcion("Venue importado desde Ticketmaster")
                                .build();
                        lugar = lugarRepository.save(nuevo);
                    }
                }

                Evento evento = Evento.builder()
                        .nombre(dto.getNombre())
                        .descripcion(dto.getDescripcion())
                        .fechaInicio(dto.getFechaInicio())
                        .fechaFin(dto.getFechaFin())
                        .imagenUrl(dto.getImagenUrl())
                        .estado(dto.getEstado() != null ? dto.getEstado() : EstadoEvento.PROXIMO)
                        .fuente(FuenteEvento.EXTERNO)
                        .idexterno(dto.getIdexterno())
                        .categoria(cat.get())
                        .lugar(lugar)
                        .build();

                eventoRepository.save(evento);
                nuevos++;
            } catch (Exception e) {
                // Si un evento falla, continúa con el siguiente
            }
        }
        return nuevos;
    }

    // ---- Mapeo ----

    private EventoDTO toDTO(Evento e) {
        EventoDTO dto = EventoDTO.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .fechaInicio(e.getFechaInicio())
                .fechaFin(e.getFechaFin())
                .imagenUrl(e.getImagenUrl())
                .estado(e.getEstado())
                .fuente(e.getFuente())
                .idexterno(e.getIdexterno())
                .build();

        if (e.getCategoria() != null) {
            dto.setCategoriaId(e.getCategoria().getId());
            dto.setCategoriaNombre(e.getCategoria().getNombre());
        }

        if (e.getLugar() != null) {
            Lugar l = e.getLugar();
            dto.setLugar(LugarDTO.builder()
                    .id(l.getId())
                    .nombre(l.getNombre())
                    .direccion(l.getDireccion())
                    .ciudad(l.getCiudad())
                    .latitud(l.getLatitud())
                    .longitud(l.getLongitud())
                    .descripcion(l.getDescripcion())
                    .build());
        }

        if (e.getTiposBoleta() != null && !e.getTiposBoleta().isEmpty()) {
            List<TipoBoletaDTO> boletas = new ArrayList<>();
            e.getTiposBoleta().forEach(tb -> boletas.add(TipoBoletaDTO.builder()
                    .id(tb.getId())
                    .nombre(tb.getNombre())
                    .precio(tb.getPrecio())
                    .cantidadDisponible(tb.getCantidadDisponible())
                    .descripcion(tb.getDescripcion())
                    .eventoId(e.getId())
                    .build()));
            dto.setTiposBoleta(boletas);
        }

        return dto;
    }
}
