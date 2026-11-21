package co.edu.unbosque.eventos.dto.ticketmaster;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO principal que mapea un evento individual de la respuesta de Ticketmaster.
 *
 * Estructura completa del JSON de Ticketmaster para un evento:
 * {
 *   "id":   "vvG1zZ4M9t6bFq",
 *   "name": "Festival de Teatro 2025",
 *   "url":  "https://www.ticketmaster.com/event/...",
 *   "images": [ { "url": "https://...", "width": 640, "height": 427 } ],
 *   "dates": {
 *     "start": { "localDate": "2025-08-15", "localTime": "20:00:00" }
 *   },
 *   "classifications": [
 *     { "segment": { "name": "Arts & Theatre" }, "genre": { "name": "Theatre" } }
 *   ],
 *   "_embedded": {
 *     "venues": [ { "name": "Teatro Colon", "address": {...}, "city": {...}, "location": {...} } ]
 *   }
 * }
 *
 * @JsonIgnoreProperties(ignoreUnknown = true) hace que Jackson ignore
 * cualquier campo del JSON que no este mapeado aqui, evitando errores.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmEventoDTO {

    /**
     * ID unico del evento en Ticketmaster.
     * Lo guardamos en "idexterno" para evitar duplicados al sincronizar.
     */
    private String id;

    /** Nombre del evento (ej. "Festival de Teatro 2025"). */
    private String name;

    /** URL del evento en el sitio de Ticketmaster (opcional, informativo). */
    private String url;

    /** Ticketmaster price ranges, when available. */
    private List<TmPriceRangeDTO> priceRanges;

    /**
     * Lista de imagenes del evento.
     * Ticketmaster devuelve varias en distintos tamaños.
     * Tomamos la primera disponible para imagenUrl.
     */
    private List<TmImageDTO> images;

    /**
     * Fechas del evento: contiene "start" con "localDate" y "localTime".
     */
    private TmDatesDTO dates;

    /**
     * Clasificaciones del evento (genero/segmento).
     * Usamos classifications[0].segment.name para asignar la categoria.
     * Ejemplos: "Arts & Theatre", "Music", "Sports"
     */
    private List<TmClassificationDTO> classifications;

    /**
     * Objeto "_embedded" que contiene los venues (lugares) del evento.
     * Se anota con @JsonProperty porque el nombre empieza con "_".
     */
    @JsonProperty("_embedded")
    private TmEmbeddedDTO embedded;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public List<TmPriceRangeDTO> getPriceRanges() { return priceRanges; }
    public void setPriceRanges(List<TmPriceRangeDTO> priceRanges) { this.priceRanges = priceRanges; }

    public List<TmImageDTO> getImages() { return images; }
    public void setImages(List<TmImageDTO> images) { this.images = images; }

    public TmDatesDTO getDates() { return dates; }
    public void setDates(TmDatesDTO dates) { this.dates = dates; }

    public List<TmClassificationDTO> getClassifications() { return classifications; }
    public void setClassifications(List<TmClassificationDTO> classifications) {
        this.classifications = classifications;
    }

    public TmEmbeddedDTO getEmbedded() { return embedded; }
    public void setEmbedded(TmEmbeddedDTO embedded) { this.embedded = embedded; }
}
