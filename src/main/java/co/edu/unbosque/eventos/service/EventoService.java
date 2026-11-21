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

/**
 * Servicio principal para operaciones sobre la entidad Evento.
 * Gestiona el catalogo completo: creacion, consulta, filtrado y eliminacion.
 *
 * -- NOTA PARA LA API EXTERNA --
 * El metodo sincronizarDesdeApiExterna() es el punto de integracion.
 * Cuando tengas la API lista, llena ese metodo con la logica de consumo.
 */
@Service
public class EventoService implements CRUDOperation<EventoDTO> {

    /** Repositorio de eventos. */
    @Autowired
    private EventoRepository eventoRepo;

    /** Repositorio de categorias para validar y asociar al evento. */
    @Autowired
    private CategoriaRepository categoriaRepo;

    /** Repositorio de lugares para validar y asociar al evento. */
    @Autowired
    private LugarRepository lugarRepo;

    /** Constructor por defecto. */
    public EventoService() {}

    @Override
    public long count() {
        return eventoRepo.count();
    }

    @Override
    public boolean exist(Long id) {
        return eventoRepo.existsById(id);
    }

    /**
     * Crea un nuevo evento en el catalogo (fuente LOCAL).
     *
     * @param data DTO con los datos del evento (debe incluir categoriaId y lugar.id validos)
     * @return 0 exito, 1 categoria no encontrada, 2 lugar no encontrado
     */
    @Override
    @Transactional
    public int create(EventoDTO data) {
        Optional<Categoria> catFound = categoriaRepo.findById(data.getCategoriaId());
        if (catFound.isEmpty()) return 1;

        Optional<Lugar> lugarFound = lugarRepo.findById(data.getLugar().getId());
        if (lugarFound.isEmpty()) return 2;

        Evento entity = new Evento();
        entity.setNombre(data.getNombre());
        entity.setDescripcion(data.getDescripcion());
        entity.setFechaInicio(data.getFechaInicio());
        entity.setFechaFin(data.getFechaFin());
        entity.setImagenUrl(data.getImagenUrl());
        entity.setEstado(data.getEstado() != null ? data.getEstado() : EstadoEvento.PROXIMO);
        entity.setFuente(FuenteEvento.LOCAL);
        entity.setCategoria(catFound.get());
        entity.setLugar(lugarFound.get());

        eventoRepo.save(entity);
        return 0;
    }

    /**
     * Retorna todos los eventos del catalogo con sus datos de lugar y categoria.
     *
     * @return Lista completa de EventoDTO
     */
    @Override
    @Transactional(readOnly = true)
    public List<EventoDTO> getAll() {
        List<Evento> lista = eventoRepo.findAllBy();
        return mapearLista(lista);
    }

    /**
     * Elimina un evento por su ID.
     *
     * @param id ID del evento
     * @return 0 exito, 1 si no existe
     */
    @Override
    @Transactional
    public int deleteById(Long id) {
        if (!eventoRepo.existsById(id)) return 1;
        eventoRepo.deleteById(id);
        return 0;
    }

    /**
     * Actualiza los datos de un evento existente.
     *
     * @param id      ID del evento a actualizar
     * @param newData DTO con los nuevos datos
     * @return 0 exito, 1 categoria no encontrada, 2 lugar no encontrado, 3 evento no existe
     */
    @Override
    @Transactional
    public int updateById(Long id, EventoDTO newData) {
        Optional<Evento> found = eventoRepo.findById(id);
        if (found.isEmpty()) return 3;

        Evento entity = found.get();

        // Actualiza categoria si se proporciona
        if (newData.getCategoriaId() != null) {
            Optional<Categoria> catFound = categoriaRepo.findById(newData.getCategoriaId());
            if (catFound.isEmpty()) return 1;
            entity.setCategoria(catFound.get());
        }

        // Actualiza lugar si se proporciona (con coordenadas para la ruta)
        if (newData.getLugar() != null && newData.getLugar().getId() != null) {
            Optional<Lugar> lugarFound = lugarRepo.findById(newData.getLugar().getId());
            if (lugarFound.isEmpty()) return 2;
            entity.setLugar(lugarFound.get());
        }

        entity.setNombre(newData.getNombre());
        entity.setDescripcion(newData.getDescripcion());
        entity.setFechaInicio(newData.getFechaInicio());
        entity.setFechaFin(newData.getFechaFin());
        entity.setImagenUrl(newData.getImagenUrl());
        if (newData.getEstado() != null) entity.setEstado(newData.getEstado());

        eventoRepo.save(entity);
        return 0;
    }

    /**
     * Obtiene un evento por su ID con todos sus datos (lugar, categoria, boletas).
     *
     * @param id ID del evento
     * @return EventoDTO completo o null si no existe
     */
    @Transactional(readOnly = true)
    public EventoDTO getById(Long id) {
        Optional<Evento> found = eventoRepo.findDetalleById(id);
        return found.map(this::mapearEvento).orElse(null);
    }

    /**
     * Filtra eventos por categoria.
     * Usado en el catalogo publico para mostrar solo Teatro, Feria, etc.
     *
     * @param categoriaId ID de la categoria
     * @return Lista de eventos de esa categoria
     */
    @Transactional(readOnly = true)
    public List<EventoDTO> getByCategoria(Long categoriaId) {
        return mapearLista(eventoRepo.findByCategoriaId(categoriaId));
    }

    /**
     * Filtra eventos por estado (ACTIVO, PROXIMO, FINALIZADO, CANCELADO).
     *
     * @param estado Estado a filtrar
     * @return Lista de eventos con ese estado
     */
    @Transactional(readOnly = true)
    public List<EventoDTO> getByEstado(EstadoEvento estado) {
        return mapearLista(eventoRepo.findByEstado(estado));
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
        return mapearLista(eventoRepo.findByLugarCiudadIgnoreCase(ciudad));
    }

    /**
     * Busca eventos cuyo nombre contenga el texto dado.
     *
     * @param nombre Texto a buscar
     * @return Lista de eventos que coincidan
     */
    @Transactional(readOnly = true)
    public List<EventoDTO> buscarPorNombre(String nombre) {
        return mapearLista(eventoRepo.findByNombreContainingIgnoreCase(nombre));
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

            // Valida que la categoria exista; si no, la salta
            if (dto.getCategoriaId() == null) continue;
            Optional<Categoria> catFound = categoriaRepo.findById(dto.getCategoriaId());
            if (catFound.isEmpty()) continue;

            // Valida que el lugar exista; si no tiene ID, lo crea en la BD
            if (dto.getLugar() == null) continue;
            Lugar lugar;
            if (dto.getLugar().getId() != null) {
                Optional<Lugar> lugarFound = lugarRepo.findById(dto.getLugar().getId());
                if (lugarFound.isEmpty()) continue;
                lugar = lugarFound.get();
            } else {
                // Lugar nuevo traido desde Ticketmaster: lo buscamos por nombre o lo creamos
                List<Lugar> existentes = lugarRepo.findByNombreContainingIgnoreCase(
                        dto.getLugar().getNombre() != null ? dto.getLugar().getNombre() : "");
                if (!existentes.isEmpty()) {
                    lugar = existentes.get(0);
                } else {
                    Lugar nuevoLugar = new Lugar();
                    nuevoLugar.setNombre(dto.getLugar().getNombre() != null ? dto.getLugar().getNombre() : "Sin nombre");
                    nuevoLugar.setDireccion(dto.getLugar().getDireccion() != null ? dto.getLugar().getDireccion() : "Sin dirección");
                    nuevoLugar.setCiudad(dto.getLugar().getCiudad() != null ? dto.getLugar().getCiudad() : "Sin ciudad");
                    nuevoLugar.setLatitud(dto.getLugar().getLatitud() != null ? dto.getLugar().getLatitud() : 0.0);
                    nuevoLugar.setLongitud(dto.getLugar().getLongitud() != null ? dto.getLugar().getLongitud() : 0.0);
                    nuevoLugar.setDescripcion("Venue importado desde Ticketmaster");
                    lugar = lugarRepo.save(nuevoLugar);
                }
            }

            // Construye y guarda el evento externo
            Evento entity = new Evento();
            entity.setNombre(dto.getNombre());
            entity.setDescripcion(dto.getDescripcion());
            entity.setFechaInicio(dto.getFechaInicio());
            entity.setFechaFin(dto.getFechaFin());
            entity.setImagenUrl(dto.getImagenUrl());
            entity.setEstado(dto.getEstado() != null ? dto.getEstado() : EstadoEvento.PROXIMO);
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
    // Metodos privados de mapeo

    /**
     * Convierte una entidad Evento a EventoDTO incluyendo lugar (con coordenadas)
     * y tipos de boleta.
     *
     * @param evento Entidad a convertir
     * @return EventoDTO con todos los datos necesarios para el frontend
     */
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

        // Mapea categoria
        if (evento.getCategoria() != null) {
            dto.setCategoriaId(evento.getCategoria().getId());
            dto.setCategoriaNombre(evento.getCategoria().getNombre());
        }

        // Mapea lugar con latitud y longitud para que el frontend genere la ruta
        if (evento.getLugar() != null) {
            Lugar l = evento.getLugar();
            LugarDTO lugarDTO = new LugarDTO(
                l.getId(), l.getNombre(), l.getDireccion(), l.getCiudad(),
                l.getLatitud(), l.getLongitud(), l.getDescripcion()
            );
            dto.setLugar(lugarDTO);
        }

        // Mapea tipos de boleta
        if (evento.getTiposBoleta() != null && !evento.getTiposBoleta().isEmpty()) {
            List<TipoBoletaDTO> boletas = new ArrayList<>();
            evento.getTiposBoleta().forEach(tb -> {
                TipoBoletaDTO tbDto = new TipoBoletaDTO(
                    tb.getId(), tb.getNombre(), tb.getPrecio(),
                    tb.getCantidadDisponible(), tb.getDescripcion(), evento.getId()
                );
                boletas.add(tbDto);
            });
            dto.setTiposBoleta(boletas);
        }

        return dto;
    }

    /**
     * Convierte una lista de entidades Evento a lista de EventoDTO.
     *
     * @param lista Lista de entidades
     * @return Lista de DTOs
     */
    private List<EventoDTO> mapearLista(List<Evento> lista) {
        List<EventoDTO> dtos = new ArrayList<>();
        lista.forEach(e -> dtos.add(mapearEvento(e)));
        return dtos;
    }
}