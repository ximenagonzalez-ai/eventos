
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
import co.edu.unbosque.eventos.model.TipoBoleta;
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

    // CREAR EVENTO LOCAL
    @Override
    @Transactional
    public int create(EventoDTO data) {

        if (data == null || data.getCategoriaId() == null) {
            return 1;
        }

        Optional<Categoria> categoria =
                categoriaRepo.findById(data.getCategoriaId());

        if (categoria.isEmpty()) {
            return 1;
        }

        if (data.getLugar() == null
                || data.getLugar().getId() == null) {
            return 2;
        }

        Optional<Lugar> lugar =
                lugarRepo.findById(data.getLugar().getId());

        if (lugar.isEmpty()) {
            return 2;
        }

        Evento entity = new Evento();

        entity.setNombre(data.getNombre());
        entity.setDescripcion(data.getDescripcion());
        entity.setFechaInicio(data.getFechaInicio());
        entity.setFechaFin(data.getFechaFin());
        entity.setImagenUrl(data.getImagenUrl());

        entity.setEstado(
                data.getEstado() != null
                        ? data.getEstado()
                        : EstadoEvento.PROXIMO);

        entity.setFuente(FuenteEvento.LOCAL);
        entity.setCategoria(categoria.get());
        entity.setLugar(lugar.get());

        eventoRepo.save(entity);

        return 0;
    }

    // CONSULTAR TODOS
    @Override
    @Transactional(readOnly = true)
    public List<EventoDTO> getAll() {

        return mapearLista(eventoRepo.findAll());
    }

    // ELIMINAR
    @Override
    @Transactional
    public int deleteById(Long id) {

        if (!eventoRepo.existsById(id)) {
            return 1;
        }

        eventoRepo.deleteById(id);

        return 0;
    }

    // ACTUALIZAR EVENTO
    @Override
    @Transactional
    public int updateById(Long id, EventoDTO newData) {

        Optional<Evento> encontrado =
                eventoRepo.findById(id);

        if (encontrado.isEmpty()) {
            return 3;
        }

        Evento entity = encontrado.get();

        if (newData.getCategoriaId() != null) {

            Optional<Categoria> categoria =
                    categoriaRepo.findById(
                            newData.getCategoriaId());

            if (categoria.isEmpty()) {
                return 1;
            }

            entity.setCategoria(categoria.get());
        }

        if (newData.getLugar() != null
                && newData.getLugar().getId() != null) {

            Optional<Lugar> lugar =
                    lugarRepo.findById(
                            newData.getLugar().getId());

            if (lugar.isEmpty()) {
                return 2;
            }

            entity.setLugar(lugar.get());
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

    // CONSULTAR POR ID
    @Transactional(readOnly = true)
    public EventoDTO getById(Long id) {

        Optional<Evento> encontrado =
                eventoRepo.findById(id);

        return encontrado
                .map(this::mapearEvento)
                .orElse(null);
    }

    // FILTROS
    @Transactional(readOnly = true)
    public List<EventoDTO> getByCategoria(Long categoriaId) {

        return mapearLista(
                eventoRepo.findByCategoriaId(categoriaId));
    }

    /**
     * Filtra eventos por estado (ACTIVO, PROXIMO, FINALIZADO, CANCELADO).
     *
     * @param estado Estado a filtrar
     * @return Lista de eventos con ese estado
     */
    @Transactional(readOnly = true)
    public List<EventoDTO> getByEstado(EstadoEvento estado) {

        return mapearLista(
                eventoRepo.findByEstado(estado));
    }

    /**
     * Filtra eventos por categoria y estado combinados.
     * Ejemplo: todos los eventos ACTIVOS de tipo Teatro.
     *
     * @param categoriaId ID de la categoria
     * @param estado      Estado del evento
     * @return Lista de eventos que cumplen ambos filtros
     */
    @Transactional(readOnly = true)
    public List<EventoDTO> getByCategoriaYEstado(Long categoriaId, EstadoEvento estado) {
        return mapearLista(eventoRepo.findByCategoriaIdAndEstado(categoriaId, estado));
    }

    /**
     * Busca eventos por ciudad del lugar.
     * El frontend puede usar esto para filtrar por ubicacion.
     *
     * @param ciudad Ciudad a buscar
     * @return Lista de eventos en esa ciudad
     */
    @Transactional(readOnly = true)
    public List<EventoDTO> getByCiudad(String ciudad) {

        return mapearLista(
                eventoRepo.findByLugarCiudadIgnoreCase(ciudad));
    }

    /**
     * Busca eventos cuyo nombre contenga el texto dado.
     *
     * @param nombre Texto a buscar
     * @return Lista de eventos que coincidan
     */
    @Transactional(readOnly = true)
    public List<EventoDTO> buscarPorNombre(String nombre) {

        return mapearLista(
                eventoRepo.findByNombreContainingIgnoreCase(nombre));
    }

    /**
     * Filtra eventos que inician en un rango de fechas.
     *
     * @param inicio Fecha inicio del rango
     * @param fin    Fecha fin del rango
     * @return Lista de eventos en ese rango
     */
    @Transactional(readOnly = true)
    public List<EventoDTO> getByRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return mapearLista(eventoRepo.findByFechaInicioBetween(inicio, fin));
    }

    // -----------------------------------------------------------------------
    // PUNTO DE INTEGRACION CON API EXTERNA
    // -----------------------------------------------------------------------

    /**
     * Sincroniza eventos desde una API externa.
     * Este metodo recibe la lista de DTOs que ya se construloo consumiendo la API
     * y los guarda en la base de datos evitando duplicados.
     *
     * 
     * 1. En la clase de cliente HTTP (EventoApiClient), consume la API externa
     *    y convierte cada respuesta en un EventoDTO con fuente = EXTERNO e idExterno = id de la API.
     * 2. Llama a este metodo pasando esa lista.
     * 3. El metodo verifica con findByIdexterno() si el evento ya existe antes de guardar.
     *
     * @param eventosExternos Lista de EventoDTO provenientes de la API externa
     * @return Numero de eventos nuevos que se guardaron
     */
    @Transactional
    public int sincronizarDesdeApiExterna(List<EventoDTO> eventosExternos) {
        int nuevos = 0;

        for (EventoDTO dto : eventosExternos) {
            // Actualiza la información externa de eventos ya sincronizados.
            if (dto.getIdexterno() != null) {
                Optional<Evento> existente = eventoRepo.findByIdexterno(dto.getIdexterno());
                if (existente.isPresent()) {
                    Evento entity = existente.get();
                    if (dto.getUrlVentaExterna() != null) {
                        entity.setUrlVentaExterna(dto.getUrlVentaExterna());
                    }
                    if (dto.getPrecioMinimoExterno() != null) {
                        entity.setPrecioMinimoExterno(dto.getPrecioMinimoExterno());
                    }
                    if (dto.getPrecioMaximoExterno() != null) {
                        entity.setPrecioMaximoExterno(dto.getPrecioMaximoExterno());
                    }
                    if (dto.getMonedaPrecioExterno() != null) {
                        entity.setMonedaPrecioExterno(dto.getMonedaPrecioExterno());
                    }
                    eventoRepo.save(entity);
                    continue;
                }
            }

            // Validar lugar
            if (dto.getLugar() == null) {
                continue;
            }

            Lugar lugar;

            if (dto.getLugar().getId() != null) {

                Optional<Lugar> encontrado =
                        lugarRepo.findById(
                                dto.getLugar().getId());

                if (encontrado.isEmpty()) {
                    continue;
                }

                lugar = encontrado.get();

            } else {

                String nombreLugar =
                        dto.getLugar().getNombre();

                if (nombreLugar == null
                        || nombreLugar.isBlank()) {
                    continue;
                }

                List<Lugar> lugares =
                        lugarRepo.findByNombreContainingIgnoreCase(
                                nombreLugar);

                if (!lugares.isEmpty()) {

                    lugar = lugares.get(0);

                } else {

                    Lugar nuevoLugar = new Lugar();

                    nuevoLugar.setNombre(nombreLugar);

                    nuevoLugar.setDireccion(
                            dto.getLugar().getDireccion() != null
                                    ? dto.getLugar().getDireccion()
                                    : "Sin direccion");

                    nuevoLugar.setCiudad(
                            dto.getLugar().getCiudad() != null
                                    ? dto.getLugar().getCiudad()
                                    : "Sin ciudad");

                    nuevoLugar.setLatitud(
                            dto.getLugar().getLatitud() != null
                                    ? dto.getLugar().getLatitud()
                                    : 0.0);

                    nuevoLugar.setLongitud(
                            dto.getLugar().getLongitud() != null
                                    ? dto.getLugar().getLongitud()
                                    : 0.0);

                    nuevoLugar.setDescripcion(
                            "Venue importado desde Ticketmaster");

                    lugar = lugarRepo.save(nuevoLugar);
                }
            }

            // Validar categoria
            if (dto.getCategoriaId() == null) {
                continue;
            }
            Optional<Categoria> catFound = categoriaRepo.findById(dto.getCategoriaId());
            if (catFound.isEmpty()) {
                continue;
            }

            // Crear evento externo
            Evento entity = new Evento();

            entity.setNombre(dto.getNombre());
            entity.setDescripcion(dto.getDescripcion());
            entity.setFechaInicio(dto.getFechaInicio());
            entity.setFechaFin(dto.getFechaFin());
            entity.setImagenUrl(dto.getImagenUrl());

            entity.setEstado(
                    dto.getEstado() != null
                            ? dto.getEstado()
                            : EstadoEvento.PROXIMO);

            entity.setFuente(FuenteEvento.EXTERNO);
            entity.setIdexterno(dto.getIdexterno());
            entity.setUrlVentaExterna(dto.getUrlVentaExterna());
            entity.setPrecioMinimoExterno(dto.getPrecioMinimoExterno());
            entity.setPrecioMaximoExterno(dto.getPrecioMaximoExterno());
            entity.setMonedaPrecioExterno(dto.getMonedaPrecioExterno());
            entity.setCategoria(catFound.get());
            entity.setLugar(lugar);

            eventoRepo.save(entity);

            nuevos++;
        }

        return nuevos;
    }

    // CONVERTIR ENTIDAD A DTO
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
        dto.setUrlVentaExterna(evento.getUrlVentaExterna());
        dto.setPrecioMinimoExterno(evento.getPrecioMinimoExterno());
        dto.setPrecioMaximoExterno(evento.getPrecioMaximoExterno());
        dto.setMonedaPrecioExterno(evento.getMonedaPrecioExterno());

        // Categoria
        if (evento.getCategoria() != null) {

            dto.setCategoriaId(
                    evento.getCategoria().getId());

            dto.setCategoriaNombre(
                    evento.getCategoria().getNombre());
        }

        // Lugar
        if (evento.getLugar() != null) {

            Lugar l = evento.getLugar();

            LugarDTO lugarDTO = new LugarDTO(
                    l.getId(),
                    l.getNombre(),
                    l.getDireccion(),
                    l.getCiudad(),
                    l.getLatitud(),
                    l.getLongitud(),
                    l.getDescripcion());

            dto.setLugar(lugarDTO);
        }

        // Tipos de boleta registrados en MySQL
        List<TipoBoletaDTO> boletas = new ArrayList<>();

        if (evento.getTiposBoleta() != null) {

            for (TipoBoleta tb : evento.getTiposBoleta()) {

                TipoBoletaDTO tbDto = new TipoBoletaDTO(
                        tb.getId(),
                        tb.getNombre(),
                        tb.getPrecio(),
                        tb.getCantidadDisponible(),
                        tb.getDescripcion(),
                        evento.getId());

                boletas.add(tbDto);
            }
        }

        dto.setTiposBoleta(boletas);

        return dto;
    }

    private List<EventoDTO> mapearLista(List<Evento> lista) {

        List<EventoDTO> resultado = new ArrayList<>();

        for (Evento evento : lista) {
            resultado.add(mapearEvento(evento));
        }

        return resultado;
    }
}