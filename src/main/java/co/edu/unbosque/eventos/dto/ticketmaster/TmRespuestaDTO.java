package co.edu.unbosque.eventos.dto.ticketmaster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO raiz que mapea la respuesta completa del endpoint de Ticketmaster:
 *   GET /discovery/v2/events.json?apikey=...
 *
 * La respuesta tiene esta estructura:
 * {
 *   "_embedded": {
 *     "events": [ { ...evento1... }, { ...evento2... } ]
 *   },
 *   "page": {
 *     "size": 20,
 *     "totalElements": 150,
 *     "totalPages": 8,
 *     "number": 0
 *   }
 * }
 *
 * Esta clase es lo que le pasamos a RestTemplate para que deserialice
 * automaticamente el JSON completo de la respuesta.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmRespuestaDTO {

    /**
     * Objeto "_embedded" de la respuesta raiz.
     * Contiene la lista de eventos retornados por la API.
     */
    @JsonProperty("_embedded")
    private TmEmbeddedRaizDTO embedded;

    /**
     * Informacion de paginacion de la respuesta.
     * Ticketmaster pagina los resultados de a 20 por defecto.
     */
    private TmPageDTO page;

    public TmEmbeddedRaizDTO getEmbedded() { return embedded; }
    public void setEmbedded(TmEmbeddedRaizDTO embedded) { this.embedded = embedded; }

    public TmPageDTO getPage() { return page; }
    public void setPage(TmPageDTO page) { this.page = page; }

    // -----------------------------------------------------------------------

    /**
     * DTO anidado que representa el "_embedded" de la respuesta RAIZ.
     * (diferente al "_embedded" dentro de cada evento que tiene "venues")
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TmEmbeddedRaizDTO {

        /** Lista de eventos retornados por la consulta. */
        private List<TmEventoDTO> events;

        public List<TmEventoDTO> getEvents() { return events; }
        public void setEvents(List<TmEventoDTO> events) { this.events = events; }
    }

    /**
     * DTO anidado para la informacion de paginacion.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TmPageDTO {

        /** Cantidad de eventos por pagina (por defecto 20). */
        private int size;

        /** Total de eventos disponibles en la busqueda. */
        private int totalElements;

        /** Total de paginas disponibles. */
        private int totalPages;

        /** Numero de pagina actual (comienza en 0). */
        private int number;

        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }

        public int getTotalElements() { return totalElements; }
        public void setTotalElements(int totalElements) { this.totalElements = totalElements; }

        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

        public int getNumber() { return number; }
        public void setNumber(int number) { this.number = number; }
    }
}
