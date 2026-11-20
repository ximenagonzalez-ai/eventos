
package co.edu.unbosque.eventos.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.eventos.dto.EventoDTO;
import co.edu.unbosque.eventos.dto.LugarDTO;
import co.edu.unbosque.eventos.dto.TipoBoletaDTO;
import co.edu.unbosque.eventos.model.Categoria;
import co.edu.unbosque.eventos.model.Evento;
import co.edu.unbosque.eventos.model.Evento.EstadoEvento;
import co.edu.unbosque.eventos.model.Evento.FuenteEvento;
import co.edu.unbosque.eventos.model.Lugar;
import co.edu.unbosque.eventos.repository.CategoriaRepository;
import co.edu.unbosque.eventos.repository.EventoRepository;
import co.edu.unbosque.eventos.repository.LugarRepository;

@Service
public class EventoService implements CRUDOperation<EventoDTO> {

    @Autowired
    private EventoRepository eventoRepo;

    @Autowired
    private CategoriaRepository categoriaRepo;

    @Autowired
    private LugarRepository lugarRepo;

    public EventoService() {}

    @Override
    public long count() {
        return eventoRepo.count();
    }

    @Override
    public boolean exist(Long id) {
        return eventoRepo.existsById(id);
    }

    @Override
    @Transactional
    public int create(EventoDTO data) {

        Optional<Categoria> catFound =
                categoriaRepo.findById(data.getCategoriaId());

        if (catFound.isEmpty()) return 1;

        Optional<Lugar> lugarFound =
                lugarRepo.findById(data.getLugar().getId());

        if (lugarFound.isEmpty()) return 2;

        Evento entity = new Evento();

        entity.setNombre(data.getNombre());
        entity.setDescripcion(data.getDescripcion());
        entity.setFechaInicio(data.getFechaInicio());
        entity.setFechaFin(data.getFechaFin());
        entity.setImagenUrl(data.getImagenUrl());

        entity.setEstado(
            data.getEstado() != null
                ? data.getEstado()
                : EstadoEvento.PROXIMO
        );

        entity.setFuente(FuenteEvento.LOCAL);
        entity.setCategoria(catFound.get());
        entity.setLugar(lugarFound.get());

        eventoRepo.save(entity);

        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventoDTO> getAll() {

        List<Evento> lista = eventoRepo.findAllBy();

        return mapearLista(lista);
    }

    @Override
    @Transactional
    public int deleteById(Long id) {

        if (!eventoRepo.existsById(id)) {
            return 1;
        }

        eventoRepo.deleteById(id);

        return 0;
    }

    @Override
    @Transactional
    public int updateById(Long id, EventoDTO newData) {

        Optional<Evento> found = eventoRepo.findById(id);

        if (found.isEmpty()) return 3;

        Evento entity = found.get();

        if (newData.getCategoriaId() != null) {

            Optional<Categoria> catFound =
                    categoriaRepo.findById(newData.getCategoriaId());

            if (catFound.isEmpty()) return 1;

            entity.setCategoria(catFound.get());
        }

        if (newData.getLugar() != null
                && newData.getLugar().getId() != null) {

            Optional<Lugar> lugarFound =
                    lugarRepo.findById(newData.getLugar().getId());

            if (lugarFound.isEmpty()) return 2;

            entity.setLugar(lugarFound.get());
        }

        entity.setNombre(newData.getNombre());
        entity.setDescripcion(newData.getDescripcion());
        entity.setFechaInicio(newData.getFechaInicio());
        entity.setFechaFin(newData.getFechaFin());
        entity.setImagenUrl(newData.getImagenUrl());

        if (newData.getEstado() != null) {
            entity.setEstado(newData.getEstado());
        }

        eventoRepo.save(entity);

        return 0;
    }

    @Transactional(readOnly = true)
    public EventoDTO getById(Long id) {

        Optional<Evento> found = eventoRepo.findById(id);

        return found.map(this::mapearEvento).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<EventoDTO> getByCategoria(Long categoriaId) {

        return mapearLista(
            eventoRepo.findByCategoriaId(categoriaId)
        );
    }

    @Transactional(readOnly = true)
    public List<EventoDTO> getByEstado(EstadoEvento estado) {

        return mapearLista(
            eventoRepo.findByEstado(estado)
        );
    }

    @Transactional(readOnly = true)
    public List<EventoDTO> getByCategoriaYEstado(
            Long categoriaId,
            EstadoEvento estado) {

        return mapearLista(
            eventoRepo.findByCategoriaIdAndEstado(
                categoriaId, estado
            )
        );
    }

    @Transactional(readOnly = true)
    public List<EventoDTO> getByCiudad(String ciudad) {

        return mapearLista(
            eventoRepo.findByLugarCiudadIgnoreCase(ciudad)
        );
    }

    @Transactional(readOnly = true)
    public List<EventoDTO> buscarPorNombre(String nombre) {

        return mapearLista(
            eventoRepo.findByNombreContainingIgnoreCase(nombre)
        );
    }

    @Transactional(readOnly = true)
    public List<EventoDTO> getByRangoFechas(
            LocalDateTime inicio,
            LocalDateTime fin) {

        return mapearLista(
            eventoRepo.findByFechaInicioBetween(inicio, fin)
        );
    }

    @Transactional
    public int sincronizarDesdeApiExterna(
            List<EventoDTO> eventosExternos) {

        int nuevos = 0;

        for (EventoDTO dto : eventosExternos) {

            if (dto.getIdexterno() != null
                    && eventoRepo.findByIdexterno(
                        dto.getIdexterno()
                    ).isPresent()) {
                continue;
            }

            if (dto.getCategoriaId() == null) continue;

            Optional<Categoria> catFound =
                    categoriaRepo.findById(dto.getCategoriaId());

            if (catFound.isEmpty()) continue;

            if (dto.getLugar() == null) continue;

            Lugar lugar;

            if (dto.getLugar().getId() != null) {

                Optional<Lugar> lugarFound =
                        lugarRepo.findById(dto.getLugar().getId());

                if (lugarFound.isEmpty()) continue;

                lugar = lugarFound.get();

            } else {

                List<Lugar> existentes =
                    lugarRepo.findByNombreContainingIgnoreCase(
                        dto.getLugar().getNombre() != null
                            ? dto.getLugar().getNombre()
                            : ""
                    );

                if (!existentes.isEmpty()) {

                    lugar = existentes.get(0);

                } else {

                    Lugar nuevoLugar = new Lugar();

                    nuevoLugar.setNombre(
                        dto.getLugar().getNombre() != null
                            ? dto.getLugar().getNombre()
                            : "Sin nombre"
                    );

                    nuevoLugar.setDireccion(
                        dto.getLugar().getDireccion() != null
                            ? dto.getLugar().getDireccion()
                            : "Sin dirección"
                    );

                    nuevoLugar.setCiudad(
                        dto.getLugar().getCiudad() != null
                            ? dto.getLugar().getCiudad()
                            : "Sin ciudad"
                    );

                    nuevoLugar.setLatitud(
                        dto.getLugar().getLatitud() != null
                            ? dto.getLugar().getLatitud()
                            : 0.0
                    );

                    nuevoLugar.setLongitud(
                        dto.getLugar().getLongitud() != null
                            ? dto.getLugar().getLongitud()
                            : 0.0
                    );

                    nuevoLugar.setDescripcion(
                        "Venue importado desde Ticketmaster"
                    );

                    lugar = lugarRepo.save(nuevoLugar);
                }
            }

            Evento entity = new Evento();

            entity.setNombre(dto.getNombre());
            entity.setDescripcion(dto.getDescripcion());
            entity.setFechaInicio(dto.getFechaInicio());
            entity.setFechaFin(dto.getFechaFin());
            entity.setImagenUrl(dto.getImagenUrl());

            entity.setEstado(
                dto.getEstado() != null
                    ? dto.getEstado()
                    : EstadoEvento.PROXIMO
            );

            entity.setFuente(FuenteEvento.EXTERNO);
            entity.setIdexterno(dto.getIdexterno());
            entity.setCategoria(catFound.get());
            entity.setLugar(lugar);

            eventoRepo.save(entity);

            nuevos++;
        }

        return nuevos;
    }

    private EventoDTO mapearEvento(Evento evento) {

        EventoDTO dto = new EventoDTO();

        dto.setId(evento.getId());
        dto.setNombre(evento.getNombre());
        dto.setDescripcion(evento.getDescripcion());
        dto.setFechaInicio(evento.getFechaInicio());
        dto.setFechaFin(evento.getFechaFin());
        dto.setImagenUrl(evento.getImagenUrl());
        dto.setEstado(evento.getEstado());
        dto.setFuente(evento.getFuente());
        dto.setIdexterno(evento.getIdexterno());

        if (evento.getCategoria() != null) {

            dto.setCategoriaId(
                evento.getCategoria().getId()
            );

            dto.setCategoriaNombre(
                evento.getCategoria().getNombre()
            );
        }

        if (evento.getLugar() != null) {

            Lugar l = evento.getLugar();

            LugarDTO lugarDTO = new LugarDTO(
                l.getId(),
                l.getNombre(),
                l.getDireccion(),
                l.getCiudad(),
                l.getLatitud(),
                l.getLongitud(),
                l.getDescripcion()
            );

            dto.setLugar(lugarDTO);
        }

        if (evento.getTiposBoleta() != null
                && !evento.getTiposBoleta().isEmpty()) {

            List<TipoBoletaDTO> boletas = new ArrayList<>();

            evento.getTiposBoleta().forEach(tb -> {

                TipoBoletaDTO tbDto = new TipoBoletaDTO(
                    tb.getId(),
                    tb.getNombre(),
                    tb.getPrecio(),
                    tb.getCantidadDisponible(),
                    tb.getDescripcion(),
                    evento.getId()
                );

                boletas.add(tbDto);
            });

            dto.setTiposBoleta(boletas);
        }

        return dto;
    }

    private List<EventoDTO> mapearLista(List<Evento> lista) {

        List<EventoDTO> dtos = new ArrayList<>();

        for (Evento evento : lista) {
            dtos.add(mapearEvento(evento));
        }

        return dtos;
    }
}
