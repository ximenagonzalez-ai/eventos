
package co.edu.unbosque.eventos.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import co.edu.unbosque.eventos.dto.EventoDTO;
import co.edu.unbosque.eventos.dto.LugarDTO;
import co.edu.unbosque.eventos.dto.ticketmaster.TmClassificationDTO;
import co.edu.unbosque.eventos.dto.ticketmaster.TmEventoDTO;
import co.edu.unbosque.eventos.dto.ticketmaster.TmPriceRangeDTO;
import co.edu.unbosque.eventos.dto.ticketmaster.TmRespuestaDTO;
import co.edu.unbosque.eventos.dto.ticketmaster.TmVenueDTO;
import co.edu.unbosque.eventos.model.Evento.EstadoEvento;
import co.edu.unbosque.eventos.model.Evento.FuenteEvento;
import co.edu.unbosque.eventos.repository.CategoriaRepository;

@Service
public class EventoApiClient {

    private static final Logger log =
            LoggerFactory.getLogger(EventoApiClient.class);

    @Value("${ticketmaster.api.key}")
    private String apiKey;

    @Value("${ticketmaster.api.url}")
    private String apiUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EventoService eventoService;

    @Autowired
    private CategoriaRepository categoriaRepo;

    public EventoApiClient() {}

    // Sincroniza eventos desde Ticketmaster
    public int sincronizar() {

        List<EventoDTO> externos = new ArrayList<>();

        externos.addAll(obtenerEventosPorCategoria("arts"));
        externos.addAll(obtenerEventosPorCategoria("music"));
        externos.addAll(obtenerEventosPorCategoria("sports"));
        externos.addAll(obtenerEventosPorKeyword("festival"));

        return eventoService.sincronizarDesdeApiExterna(externos);
    }

    public List<EventoDTO> obtenerEventosPorKeyword(String keyword) {

        String url = construirUrl("keyword", keyword);

        return consultarEventos(url);
    }

    public List<EventoDTO> obtenerEventosExternos() {

        String url = construirUrl("page", "0");

        return consultarEventos(url);
    }

    public List<EventoDTO> obtenerEventosPorCategoria(
            String clasificacion) {

        String url = construirUrl(
                "classificationName", clasificacion);

        return consultarEventos(url);
    }

    // Construye URL sin colocar la API Key directamente
    // en el codigo fuente.
    private String construirUrl(String parametro, String valor) {

        return UriComponentsBuilder
                .fromUriString(apiUrl + "/events.json")
                .queryParam("apikey", apiKey)
                .queryParam("size", 20)
                .queryParam(parametro, valor)
                .build()
                .encode()
                .toUriString();
    }

    private List<EventoDTO> consultarEventos(String url) {

        try {

            TmRespuestaDTO respuesta =
                    restTemplate.getForObject(
                            url, TmRespuestaDTO.class);

            if (respuesta == null
                    || respuesta.getEmbedded() == null
                    || respuesta.getEmbedded().getEvents() == null) {

                log.warn("Ticketmaster no devolvio eventos");
                return new ArrayList<>();
            }

            return convertirEventos(
                    respuesta.getEmbedded().getEvents());

        } catch (Exception e) {

            log.error(
                    "Error al consultar Ticketmaster: {}",
                    e.getMessage());

            return new ArrayList<>();
        }
    }

    private List<EventoDTO> convertirEventos(
            List<TmEventoDTO> tmEventos) {

        List<EventoDTO> resultado = new ArrayList<>();

        for (TmEventoDTO tm : tmEventos) {

            try {

                EventoDTO dto = convertirEvento(tm);

                if (dto != null) {
                    resultado.add(dto);
                }

            } catch (Exception e) {

                log.warn(
                        "Error al convertir evento '{}': {}",
                        tm.getName(),
                        e.getMessage());
            }
        }

        return resultado;
    }

    private EventoDTO convertirEvento(TmEventoDTO tm) {

        if (tm.getId() == null || tm.getName() == null) {
            return null;
        }

        EventoDTO dto = new EventoDTO();

        // Identificador externo
        dto.setIdexterno(tm.getId());

        // Nombre
        dto.setNombre(tm.getName());

        // URL oficial del evento
        dto.setUrlVentaExterna(tm.getUrl());

        // Imagen
        if (tm.getImages() != null
                && !tm.getImages().isEmpty()) {

            dto.setImagenUrl(
                    tm.getImages().get(0).getUrl());
        }

        // Fechas
        if (tm.getDates() != null
                && tm.getDates().getStart() != null) {

            LocalDateTime fechaInicio = parsearFecha(
                    tm.getDates().getStart().getLocalDate(),
                    tm.getDates().getStart().getLocalTime());

            if (fechaInicio != null) {

                dto.setFechaInicio(fechaInicio);

                // Duracion estimada utilizada por el proyecto
                dto.setFechaFin(fechaInicio.plusHours(3));
            }
        }

        dto.setEstado(EstadoEvento.PROXIMO);
        dto.setFuente(FuenteEvento.EXTERNO);
        dto.setUrlVentaExterna(tm.getUrl());

        if (tm.getPriceRanges() != null) {
            for (TmPriceRangeDTO rango : tm.getPriceRanges()) {
                if (rango == null) continue;
                if (rango.getMin() != null
                        && (dto.getPrecioMinimoExterno() == null
                        || rango.getMin() < dto.getPrecioMinimoExterno())) {
                    dto.setPrecioMinimoExterno(rango.getMin());
                }
                if (rango.getMax() != null
                        && (dto.getPrecioMaximoExterno() == null
                        || rango.getMax() > dto.getPrecioMaximoExterno())) {
                    dto.setPrecioMaximoExterno(rango.getMax());
                }
                if (dto.getMonedaPrecioExterno() == null && rango.getCurrency() != null) {
                    dto.setMonedaPrecioExterno(rango.getCurrency());
                }
            }
        }

        // Categoria
        dto.setCategoriaId(resolverCategoria(tm));

        // Lugar
        dto.setLugar(resolverLugar(tm));

        // Precios reales informados por Ticketmaster
        procesarPrecios(tm, dto);

        return dto;
    }

    private void procesarPrecios(
            TmEventoDTO tm,
            EventoDTO dto) {

        if (tm.getPriceRanges() == null
                || tm.getPriceRanges().isEmpty()) {

            return;
        }

        Double minimo = null;
        Double maximo = null;
        String moneda = null;

        for (TmPriceRangeDTO rango : tm.getPriceRanges()) {

            if (rango == null) {
                continue;
            }

            // Solo combinamos rangos de la misma moneda
            if (rango.getCurrency() == null
                    || rango.getCurrency().isBlank()) {
                continue;
            }

            if (moneda == null) {
                moneda = rango.getCurrency();
            }

            if (!moneda.equals(rango.getCurrency())) {
                continue;
            }

            if (rango.getMin() != null
                    && rango.getMin() >= 0) {

                if (minimo == null
                        || rango.getMin() < minimo) {

                    minimo = rango.getMin();
                }
            }

            if (rango.getMax() != null
                    && rango.getMax() >= 0) {

                if (maximo == null
                        || rango.getMax() > maximo) {

                    maximo = rango.getMax();
                }
            }
        }

        if (minimo != null || maximo != null) {

            dto.setPrecioMinimoExterno(minimo);
            dto.setPrecioMaximoExterno(maximo);
            dto.setMonedaPrecioExterno(moneda);
        }
    }

    private LocalDateTime parsearFecha(
            String fecha, String hora) {

        if (fecha == null || fecha.isBlank()) {
            return null;
        }

        LocalDate localDate = LocalDate.parse(fecha);

        LocalTime localTime =
                hora != null && !hora.isBlank()
                        ? LocalTime.parse(hora)
                        : LocalTime.MIDNIGHT;

        return LocalDateTime.of(localDate, localTime);
    }

    private Long resolverCategoria(TmEventoDTO tm) {

        if (tm.getClassifications() == null
                || tm.getClassifications().isEmpty()) {

            return null;
        }

        TmClassificationDTO clasificacion =
                tm.getClassifications().get(0);

        if (clasificacion.getSegment() == null
                || clasificacion.getSegment().getName() == null) {

            return null;
        }

        String segmento =
                clasificacion.getSegment().getName();

        String nombreCategoria = switch (segmento) {

            case "Arts & Theatre" -> "Teatro";
            case "Music" -> "Concierto";
            case "Sports" -> "Deporte";
            case "Family" -> "Familiar";
            case "Film" -> "Cine";
            default -> "General";
        };

        return categoriaRepo
                .findByNombreIgnoreCase(nombreCategoria)
                .map(c -> c.getId())
                .orElse(null);
    }

    private LugarDTO resolverLugar(TmEventoDTO tm) {

        if (tm.getEmbedded() == null
                || tm.getEmbedded().getVenues() == null
                || tm.getEmbedded().getVenues().isEmpty()) {

            return null;
        }

        TmVenueDTO venue =
                tm.getEmbedded().getVenues().get(0);

        LugarDTO lugarDTO = new LugarDTO();

        lugarDTO.setNombre(
                venue.getName() != null
                        ? venue.getName()
                        : "Sin nombre");

        lugarDTO.setDireccion(
                venue.getAddress() != null
                && venue.getAddress().getLine1() != null
                        ? venue.getAddress().getLine1()
                        : "Sin direccion");

        lugarDTO.setCiudad(
                venue.getCity() != null
                && venue.getCity().getName() != null
                        ? venue.getCity().getName()
                        : "Sin ciudad");

        if (venue.getLocation() != null) {

            try {

                lugarDTO.setLatitud(
                        Double.parseDouble(
                                venue.getLocation().getLatitude()));

                lugarDTO.setLongitud(
                        Double.parseDouble(
                                venue.getLocation().getLongitude()));

            } catch (NumberFormatException
                    | NullPointerException e) {

                lugarDTO.setLatitud(0.0);
                lugarDTO.setLongitud(0.0);
            }

        } else {

            lugarDTO.setLatitud(0.0);
            lugarDTO.setLongitud(0.0);
        }

        return lugarDTO;
    }
}
