package co.edu.unbosque.eventos.service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import co.edu.unbosque.eventos.dto.EventoDTO;
import co.edu.unbosque.eventos.dto.LugarDTO;
import co.edu.unbosque.eventos.dto.ticketmaster.TmClassificationDTO;
import co.edu.unbosque.eventos.dto.ticketmaster.TmEventoDTO;
import co.edu.unbosque.eventos.dto.ticketmaster.TmRespuestaDTO;
import co.edu.unbosque.eventos.dto.ticketmaster.TmVenueDTO;
import co.edu.unbosque.eventos.model.Evento.EstadoEvento;
import co.edu.unbosque.eventos.model.Evento.FuenteEvento;
import co.edu.unbosque.eventos.repository.CategoriaRepository;

/**
 * Cliente HTTP que consume la API de Ticketmaster Discovery v2.
 *
 * FLUJO COMPLETO:
 * 1. El admin llama a POST /evento/sync en Swagger
 * 2. EventoController llama a this.sincronizar()
 * 3. Este metodo llama a obtenerEventosExternos() que consulta Ticketmaster
 * 4. La respuesta se convierte de TmEventoDTO -> EventoDTO
 * 5. EventoService.sincronizarDesdeApiExterna() guarda los eventos nuevos en BD
 *
 * CREDENCIALES (configuradas en application.properties):
 *   ticketmaster.api.key = tu Consumer Key
 *   ticketmaster.api.url = https://app.ticketmaster.com/discovery/v2
 */
@Service
public class EventoApiClient {

    /** Logger para registrar errores y resultados de la sincronizacion. */
    private static final Logger log = LoggerFactory.getLogger(EventoApiClient.class);

    /**
     * Consumer Key de Ticketmaster.
     * Se lee desde application.properties: ticketmaster.api.key
     * NUNCA la pongas directamente en el codigo fuente.
     */
    @Value("${ticketmaster.api.key}")
    private String apiKey;

    /**
     * URL base de la API de Ticketmaster.
     * Se lee desde application.properties: ticketmaster.api.url
     * Valor: https://app.ticketmaster.com/discovery/v2
     */
    @Value("${ticketmaster.api.url}")
    private String apiUrl;

    /**
     * RestTemplate para hacer peticiones HTTP GET a Ticketmaster.
     * Esta declarado como @Bean en AppConfig.
     */
    @Autowired
    private RestTemplate restTemplate;

    /**
     * Servicio de eventos para guardar los resultados en la BD.
     */
    @Autowired
    private EventoService eventoService;

    /**
     * Repositorio de categorias para buscar el ID de la categoria
     * que corresponde al segmento que devuelve Ticketmaster.
     */
    @Autowired
    private CategoriaRepository categoriaRepo;

    /** Constructor por defecto. */
    public EventoApiClient() {}

    // METODO PRINCIPAL DE SINCRONIZACION
    
    /**
     * Punto de entrada de la sincronizacion.
     * Lo llama EventoController cuando el admin hace POST /evento/sync.
     *
     * @return Numero de eventos nuevos guardados en la BD
     */
    public int sincronizar() {
        // Lista donde acumulamos todos los eventos traidos de Ticketmaster
        List<EventoDTO> externos = new ArrayList<>();

        // Trae eventos de teatro
        //    Estos se guardan con categoria "Teatro" en la BD
        externos.addAll(obtenerEventosPorCategoria("arts"));

        // Trae conciertos
        //    Estos se guardan con categoria "Concierto" en la BD
        externos.addAll(obtenerEventosPorCategoria("music"));

        // Trae eventos deportivos (Sports en Ticketmaster)
        //    Estos se guardan con categoria "Deporte" en la BD
        externos.addAll(obtenerEventosPorCategoria("sports"));

        // Trae festivales de musica buscando por la palabra "festival"
        externos.addAll(obtenerEventosPorKeyword("festival"));

        // Envia todos los eventos a EventoService para guardarlos en la BD
        // El servicio evita duplicados usando el idexterno de cada evento
        return eventoService.sincronizarDesdeApiExterna(externos);
    }


    public List<EventoDTO> obtenerEventosPorKeyword(String keyword) {
        // Construye la URL con el parametro keyword
        String url = apiUrl + "/events.json"
                + "?apikey=" + apiKey
                + "&size=20"       // trae 20 resultados
                + "&keyword=" + keyword; // filtra por palabra clave

        log.info("Consultando Ticketmaster por keyword '{}': {}", keyword, url);

        try {
            // Llama a Ticketmaster y convierte la respuesta automaticamente
            TmRespuestaDTO respuesta = restTemplate.getForObject(url, TmRespuestaDTO.class);

            // Si no hay resultados, retorna lista vacia sin lanzar error
            if (respuesta == null
                    || respuesta.getEmbedded() == null
                    || respuesta.getEmbedded().getEvents() == null) {
                return new ArrayList<>();
            }

            // Convierte los eventos de formato Ticketmaster a formato del proyecto
            return convertirEventos(respuesta.getEmbedded().getEvents());
        } catch (Exception e) {
            log.error("Error al consultar Ticketmaster por keyword: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<EventoDTO> obtenerEventosExternos() {
        // Construye la URL con los parametros de busqueda
        // Puedes agregar &countryCode=CO para filtrar por Colombia
        String url = apiUrl + "/events.json"
                + "?apikey=" + apiKey
                + "&size=20"
                + "&page=0";

        log.info("Consultando Ticketmaster: {}", url);

        try {
            // Hace el GET y deserializa el JSON en TmRespuestaDTO automaticamente
            TmRespuestaDTO respuesta = restTemplate.getForObject(url, TmRespuestaDTO.class);

            // Si no hay respuesta o no hay eventos, retorna lista vacia
            if (respuesta == null
                    || respuesta.getEmbedded() == null
                    || respuesta.getEmbedded().getEvents() == null) {
                log.warn("Ticketmaster no devolvio eventos");
                return new ArrayList<>();
            }

            List<TmEventoDTO> eventosTicketmaster = respuesta.getEmbedded().getEvents();
            log.info("Ticketmaster devolvio {} eventos", eventosTicketmaster.size());

            // Convierte cada TmEventoDTO a EventoDTO del proyecto
            return convertirEventos(eventosTicketmaster);

        } catch (Exception e) {
            log.error("Error al consultar Ticketmaster: {}", e.getMessage());
            return new ArrayList<>();
        }
    }


    public List<EventoDTO> obtenerEventosPorCategoria(String clasificacion) {
        String url = apiUrl + "/events.json"
                + "?apikey=" + apiKey
                + "&size=20"
                + "&classificationName=" + clasificacion;

        log.info("Consultando Ticketmaster por categoria '{}': {}", clasificacion, url);

        try {
            TmRespuestaDTO respuesta = restTemplate.getForObject(url, TmRespuestaDTO.class);

            if (respuesta == null
                    || respuesta.getEmbedded() == null
                    || respuesta.getEmbedded().getEvents() == null) {
                return new ArrayList<>();
            }

            return convertirEventos(respuesta.getEmbedded().getEvents());

        } catch (Exception e) {
            log.error("Error al consultar Ticketmaster por categoria: {}", e.getMessage());
            return new ArrayList<>();
        }
    }


    /**
     * Convierte una lista de TmEventoDTO (formato Ticketmaster)
     * a una lista de EventoDTO (formato del proyecto).
     *
     * @param tmEventos Lista de eventos de Ticketmaster
     * @return Lista de EventoDTO listos para sincronizar
     */
    private List<EventoDTO> convertirEventos(List<TmEventoDTO> tmEventos) {
        List<EventoDTO> resultado = new ArrayList<>();
        for (TmEventoDTO tm : tmEventos) {
            try {
                EventoDTO dto = convertirEvento(tm);
                if (dto != null) {
                    resultado.add(dto);
                }
            } catch (Exception e) {
                // Si un evento falla, lo salta y continua con el siguiente
                log.warn("Error al convertir evento '{}': {}", tm.getName(), e.getMessage());
            }
        }
        return resultado;
    }

    /**
     * Convierte un TmEventoDTO individual a EventoDTO.
     *
     * Mapeo de campos:
     *   tm.id                              -> eventoDTO.idexterno
     *   tm.name                            -> eventoDTO.nombre
     *   tm.images[0].url                   -> eventoDTO.imagenUrl
     *   tm.dates.start.localDate+localTime -> eventoDTO.fechaInicio y fechaFin
     *   tm.classifications[0].segment.name -> eventoDTO.categoriaNombre (para buscar en BD)
     *   tm._embedded.venues[0]             -> eventoDTO.lugar (con lat/lng)
     *
     * @param tm Evento de Ticketmaster
     * @return EventoDTO listo para sincronizar, o null si faltan datos esenciales
     */
    private EventoDTO convertirEvento(TmEventoDTO tm) {
        // Si no tiene nombre o ID, no se puede usar
        if (tm.getId() == null || tm.getName() == null) return null;

        EventoDTO dto = new EventoDTO();

        // --- ID externo: se usa para evitar duplicados en BD ---
        dto.setIdexterno(tm.getId());

        // --- Nombre del evento ---
        dto.setNombre(tm.getName());

        // --- Imagen: toma la primera disponible ---
        if (tm.getImages() != null && !tm.getImages().isEmpty()) {
            dto.setImagenUrl(tm.getImages().get(0).getUrl());
        }

        // --- Fecha de inicio y fin ---
        // Ticketmaster solo da fecha de inicio; ponemos +3 horas como duracion estimada
        if (tm.getDates() != null && tm.getDates().getStart() != null) {
            LocalDateTime fechaInicio = parsearFecha(
                tm.getDates().getStart().getLocalDate(),
                tm.getDates().getStart().getLocalTime()
            );
            dto.setFechaInicio(fechaInicio);
            // Duracion estimada de 3 horas (Ticketmaster no siempre da fecha de fin)
            dto.setFechaFin(fechaInicio.plusHours(3));
        }

        // --- Estado: todos los eventos de la API se marcan como PROXIMO ---
        dto.setEstado(EstadoEvento.PROXIMO);

        // --- Fuente: marca que viene de Ticketmaster ---
        dto.setFuente(FuenteEvento.EXTERNO);

        // --- Categoria: busca en BD segun el segmento de Ticketmaster ---
        Long categoriaId = resolverCategoria(tm);
        dto.setCategoriaId(categoriaId);

        // --- Lugar: extrae el venue con coordenadas para el mapa ---
        LugarDTO lugarDTO = resolverLugar(tm);
        dto.setLugar(lugarDTO);

        return dto;
    }

    /**
     * Convierte fecha y hora de String a LocalDateTime.
     *
     * Ticketmaster devuelve:
     *   localDate = "2025-08-15"
     *   localTime = "20:00:00"  (puede ser null)
     *
     * Si no hay hora disponible, usa las 00:00:00 por defecto.
     *
     * @param fecha String con formato "yyyy-MM-dd"
     * @param hora  String con formato "HH:mm:ss" (puede ser null)
     * @return LocalDateTime combinado
     */
    private LocalDateTime parsearFecha(String fecha, String hora) {
        if (fecha == null) return LocalDateTime.now();
        LocalDate localDate = LocalDate.parse(fecha);
        LocalTime localTime = (hora != null && !hora.isEmpty())
                ? LocalTime.parse(hora)
                : LocalTime.of(0, 0);
        return LocalDateTime.of(localDate, localTime);
    }

    /**
     * Determina la categoria del evento buscando en la BD por el nombre
     * del segmento que devuelve Ticketmaster.
     *
     * Mapeo de segmentos de Ticketmaster a categorias de la BD:
     *   "Arts & Theatre" -> busca "Teatro" en la BD
     *   "Music"          -> busca "Concierto"
     *   "Sports"         -> busca "Deporte"
     *   (otros)          -> busca "General" o retorna null
     *
     * IMPORTANTE: las categorias deben existir en la BD antes de sincronizar.
     * Crealas via POST /categoria/create con esos nombres.
     *
     * @param tm Evento de Ticketmaster
     * @return ID de la categoria en la BD, o null si no se encuentra
     */
    private Long resolverCategoria(TmEventoDTO tm) {
        if (tm.getClassifications() == null || tm.getClassifications().isEmpty()) return null;

        TmClassificationDTO clasificacion = tm.getClassifications().get(0);
        if (clasificacion.getSegment() == null) return null;

        // Nombre del segmento que da Ticketmaster
        String segmento = clasificacion.getSegment().getName();

        String nombreCategoria = switch (segmento) {
            case "Arts & Theatre" -> "Teatro";
            case "Music"          -> "Concierto";
            case "Sports"         -> "Deporte";
            case "Family"         -> "Familiar";
            case "Film"           -> "Cine";
            default               -> "General";
        };

        // Busca la categoria en la BD por nombre
        return categoriaRepo.findByNombreIgnoreCase(nombreCategoria)
                .map(c -> c.getId())
                .orElse(null); // null si la categoria no existe en BD
    }

    /**
     * Extrae el venue del evento de Ticketmaster y lo convierte a LugarDTO.
     *
     * El LugarDTO incluye latitud y longitud que el frontend usa
     * para mostrar la ubicacion y la ruta en el mapa.
     *
     * Si el venue no tiene coordenadas, el lugar se crea sin lat/lng (0.0).
     *
     * @param tm Evento de Ticketmaster
     * @return LugarDTO con datos del venue, o null si no hay venue
     */
    private LugarDTO resolverLugar(TmEventoDTO tm) {
        if (tm.getEmbedded() == null
                || tm.getEmbedded().getVenues() == null
                || tm.getEmbedded().getVenues().isEmpty()) {
            return null;
        }

        // Toma el primer venue del evento
        TmVenueDTO venue = tm.getEmbedded().getVenues().get(0);

        LugarDTO lugarDTO = new LugarDTO();
        lugarDTO.setNombre(venue.getName() != null ? venue.getName() : "Sin nombre");

        // Direccion: viene en address.line1
        lugarDTO.setDireccion(
            (venue.getAddress() != null && venue.getAddress().getLine1() != null)
                ? venue.getAddress().getLine1()
                : "Sin direccion"
        );

        // Ciudad: viene en city.name
        lugarDTO.setCiudad(
            (venue.getCity() != null && venue.getCity().getName() != null)
                ? venue.getCity().getName()
                : "Sin ciudad"
        );

        // Coordenadas: vienen como String, hay que convertirlas a Double
        // Estas son las que el frontend usa para mostrar la ruta en el mapa
        if (venue.getLocation() != null) {
            try {
                lugarDTO.setLatitud(Double.parseDouble(venue.getLocation().getLatitude()));
                lugarDTO.setLongitud(Double.parseDouble(venue.getLocation().getLongitude()));
            } catch (NumberFormatException e) {
                // Si no se pueden parsear, pone 0.0 por defecto
                lugarDTO.setLatitud(0.0);
                lugarDTO.setLongitud(0.0);
                log.warn("No se pudieron parsear las coordenadas del venue '{}'", venue.getName());
            }
        } else {
            lugarDTO.setLatitud(0.0);
            lugarDTO.setLongitud(0.0);
        }

        return lugarDTO;
    }
}
