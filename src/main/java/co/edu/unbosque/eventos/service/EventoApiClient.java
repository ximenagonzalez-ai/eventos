package co.edu.unbosque.eventos.service;

import co.edu.unbosque.eventos.DTO.ticketmaster.TmClassificationDTO;
import co.edu.unbosque.eventos.DTO.ticketmaster.TmEventoDTO;
import co.edu.unbosque.eventos.DTO.ticketmaster.TmRespuestaDTO;
import co.edu.unbosque.eventos.DTO.ticketmaster.TmVenueDTO;
import co.edu.unbosque.eventos.dto.EventoDTO;
import co.edu.unbosque.eventos.dto.LugarDTO;
import co.edu.unbosque.eventos.model.Evento.EstadoEvento;
import co.edu.unbosque.eventos.model.Evento.FuenteEvento;
import co.edu.unbosque.eventos.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente HTTP que consume la API de Ticketmaster Discovery v2 para importar eventos.
 *
 * ¿Cómo funciona?
 *   Un admin llama a POST /evento/sync desde Swagger.
 *   → EventoController llama a sincronizar()
 *   → Aquí consultamos Ticketmaster en 4 categorías: arts, music, sports y "festival"
 *   → Convertimos cada respuesta de Ticketmaster (TmEventoDTO) a nuestro formato (EventoDTO)
 *   → EventoService.sincronizarDesdeApiExterna() los guarda en la BD evitando duplicados
 *
 * Credenciales (en application.properties):
 *   ticketmaster.api.key = tu Consumer Key de https://developer.ticketmaster.com
 *   ticketmaster.api.url = https://app.ticketmaster.com/discovery/v2
 *
 * IMPORTANTE: antes de sincronizar, crea las categorías en la BD con estos nombres exactos:
 *   "Teatro", "Concierto", "Deporte", "Familiar", "Cine", "General"
 *   Si no existen, los eventos de esa categoría se omiten.
 */
@Service
@RequiredArgsConstructor
public class EventoApiClient {

    private static final Logger log = LoggerFactory.getLogger(EventoApiClient.class);

    @Value("${ticketmaster.api.key}")
    private String apiKey;

    @Value("${ticketmaster.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final EventoService eventoService;
    private final CategoriaRepository categoriaRepository;

    /**
     * Punto de entrada principal. El admin llama a POST /evento/sync y llega aquí.
     * Consulta Ticketmaster en 4 pasadas y guarda los eventos nuevos en la BD.
     * Retorna cuántos eventos nuevos se guardaron.
     */
    public int sincronizar() {
        List<EventoDTO> externos = new ArrayList<>();

        // Trae teatro/artes (Ticketmaster lo llama "arts")
        externos.addAll(obtenerEventosPorCategoria("arts"));

        // Trae conciertos (Ticketmaster lo llama "music")
        externos.addAll(obtenerEventosPorCategoria("music"));

        // Trae deportes
        externos.addAll(obtenerEventosPorCategoria("sports"));

        // Trae festivales (Ticketmaster no tiene categoría separada, buscamos por keyword)
        externos.addAll(obtenerEventosPorKeyword("festival"));

        return eventoService.sincronizarDesdeApiExterna(externos);
    }

    /**
     * Busca eventos en Ticketmaster filtrando por tipo (classificationName).
     * Ejemplos válidos: "arts", "music", "sports", "family", "film"
     */
    public List<EventoDTO> obtenerEventosPorCategoria(String clasificacion) {
        String url = apiUrl + "/events.json?apikey=" + apiKey
                + "&size=20&classificationName=" + clasificacion;
        log.info("Consultando Ticketmaster por categoría '{}': {}", clasificacion, url);
        return fetchEventos(url);
    }

    /**
     * Busca eventos en Ticketmaster por palabra clave en el nombre.
     * Útil para festivales, circo, stand-up, etc.
     */
    public List<EventoDTO> obtenerEventosPorKeyword(String keyword) {
        String url = apiUrl + "/events.json?apikey=" + apiKey
                + "&size=20&keyword=" + keyword;
        log.info("Consultando Ticketmaster por keyword '{}': {}", keyword, url);
        return fetchEventos(url);
    }

    /** Hace el GET a la URL y convierte la respuesta a nuestra lista de EventoDTO. */
    private List<EventoDTO> fetchEventos(String url) {
        try {
            TmRespuestaDTO respuesta = restTemplate.getForObject(url, TmRespuestaDTO.class);
            if (respuesta == null || respuesta.getEmbedded() == null
                    || respuesta.getEmbedded().getEvents() == null) {
                return new ArrayList<>();
            }
            return convertirEventos(respuesta.getEmbedded().getEvents());
        } catch (Exception e) {
            log.error("Error consultando Ticketmaster: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /** Convierte la lista entera de formato Ticketmaster al nuestro. */
    private List<EventoDTO> convertirEventos(List<TmEventoDTO> tmEventos) {
        List<EventoDTO> resultado = new ArrayList<>();
        for (TmEventoDTO tm : tmEventos) {
            try {
                EventoDTO dto = convertirEvento(tm);
                if (dto != null) resultado.add(dto);
            } catch (Exception e) {
                log.warn("Error convirtiendo evento '{}': {}", tm.getName(), e.getMessage());
            }
        }
        return resultado;
    }

    /**
     * Convierte un evento individual de Ticketmaster al formato de nuestro proyecto.
     *
     * Mapeo de campos:
     *   tm.id               → dto.idexterno  (para evitar duplicados en la BD)
     *   tm.name             → dto.nombre
     *   tm.images[0].url    → dto.imagenUrl
     *   tm.dates.start      → dto.fechaInicio  (fechaFin = fechaInicio + 3 horas estimado)
     *   tm.classifications  → dto.categoriaId  (buscamos en nuestra BD por nombre)
     *   tm._embedded.venues → dto.lugar        (con latitud y longitud para el mapa)
     */
    private EventoDTO convertirEvento(TmEventoDTO tm) {
        if (tm.getId() == null || tm.getName() == null) return null;

        EventoDTO dto = new EventoDTO();
        dto.setIdexterno(tm.getId());
        dto.setNombre(tm.getName());

        if (tm.getImages() != null && !tm.getImages().isEmpty()) {
            dto.setImagenUrl(tm.getImages().get(0).getUrl());
        }

        if (tm.getDates() != null && tm.getDates().getStart() != null) {
            LocalDateTime fechaInicio = parsearFecha(
                    tm.getDates().getStart().getLocalDate(),
                    tm.getDates().getStart().getLocalTime());
            dto.setFechaInicio(fechaInicio);
            dto.setFechaFin(fechaInicio.plusHours(3)); // Ticketmaster no siempre da fecha fin
        }

        dto.setEstado(EstadoEvento.PROXIMO); // todos los importados comienzan como "próximo"
        dto.setFuente(FuenteEvento.EXTERNO);
        dto.setCategoriaId(resolverCategoria(tm));
        dto.setLugar(resolverLugar(tm));

        return dto;
    }

    /** Parsea la fecha y hora que Ticketmaster da como strings ("2025-08-15", "20:00:00"). */
    private LocalDateTime parsearFecha(String fecha, String hora) {
        if (fecha == null) return LocalDateTime.now();
        LocalDate localDate = LocalDate.parse(fecha);
        LocalTime localTime = (hora != null && !hora.isEmpty())
                ? LocalTime.parse(hora)
                : LocalTime.of(0, 0);
        return LocalDateTime.of(localDate, localTime);
    }

    /**
     * Mapea el segmento de Ticketmaster al nombre de categoría que usamos en nuestra BD
     * y busca el ID. Si la categoría no existe, retorna null y ese evento se omite.
     *
     * Recuerda: debes crear estas categorías antes de sincronizar:
     *   Teatro, Concierto, Deporte, Familiar, Cine, General
     */
    private Long resolverCategoria(TmEventoDTO tm) {
        if (tm.getClassifications() == null || tm.getClassifications().isEmpty()) return null;
        TmClassificationDTO clasificacion = tm.getClassifications().get(0);
        if (clasificacion.getSegment() == null) return null;

        String nombreCategoria = switch (clasificacion.getSegment().getName()) {
            case "Arts & Theatre" -> "Teatro";
            case "Music"          -> "Concierto";
            case "Sports"         -> "Deporte";
            case "Family"         -> "Familiar";
            case "Film"           -> "Cine";
            default               -> "General";
        };

        return categoriaRepository.findByNombreIgnoreCase(nombreCategoria)
                .map(c -> c.getId())
                .orElse(null);
    }

    /**
     * Extrae el venue (lugar) del evento de Ticketmaster y lo convierte a LugarDTO.
     * Incluye latitud y longitud para que el frontend muestre la ubicación en el mapa.
     * Si el venue no tiene coordenadas, se guardan como 0.0.
     */
    private LugarDTO resolverLugar(TmEventoDTO tm) {
        if (tm.getEmbedded() == null || tm.getEmbedded().getVenues() == null
                || tm.getEmbedded().getVenues().isEmpty()) {
            return null;
        }

        TmVenueDTO venue = tm.getEmbedded().getVenues().get(0);
        LugarDTO lugar = new LugarDTO();
        lugar.setNombre(venue.getName() != null ? venue.getName() : "Sin nombre");
        lugar.setDireccion(venue.getAddress() != null && venue.getAddress().getLine1() != null
                ? venue.getAddress().getLine1() : "Sin dirección");
        lugar.setCiudad(venue.getCity() != null && venue.getCity().getName() != null
                ? venue.getCity().getName() : "Sin ciudad");

        if (venue.getLocation() != null) {
            try {
                lugar.setLatitud(Double.parseDouble(venue.getLocation().getLatitude()));
                lugar.setLongitud(Double.parseDouble(venue.getLocation().getLongitude()));
            } catch (NumberFormatException e) {
                lugar.setLatitud(0.0);
                lugar.setLongitud(0.0);
                log.warn("No se pudieron parsear coordenadas del venue '{}'", venue.getName());
            }
        } else {
            lugar.setLatitud(0.0);
            lugar.setLongitud(0.0);
        }

        return lugar;
    }
}
