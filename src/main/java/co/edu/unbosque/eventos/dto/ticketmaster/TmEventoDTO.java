package co.edu.unbosque.eventos.dto.ticketmaster;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TmEventoDTO {

    private String id;
    private String name;
    private String url;

    /** Ticketmaster price ranges, when available. */
    private List<TmPriceRangeDTO> priceRanges;

    /**
     * Lista de imagenes del evento.
     * Ticketmaster devuelve varias en distintos tamaños.
     * Tomamos la primera disponible para imagenUrl.
     */
    private List<TmImageDTO> images;
    private TmDatesDTO dates;
    private List<TmClassificationDTO> classifications;

    private List<TmPriceRangeDTO> priceRanges;

    @JsonProperty("_embedded")
    private TmEmbeddedDTO embedded;

    public TmEventoDTO() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<TmImageDTO> getImages() {
        return images;
    }

    public void setImages(List<TmImageDTO> images) {
        this.images = images;
    }

    public TmDatesDTO getDates() {
        return dates;
    }

    public List<TmPriceRangeDTO> getPriceRanges() { return priceRanges; }
    public void setPriceRanges(List<TmPriceRangeDTO> priceRanges) { this.priceRanges = priceRanges; }

    public List<TmImageDTO> getImages() { return images; }
    public void setImages(List<TmImageDTO> images) { this.images = images; }

    public List<TmClassificationDTO> getClassifications() {
        return classifications;
    }

    public void setClassifications(
            List<TmClassificationDTO> classifications) {
        this.classifications = classifications;
    }

    public List<TmPriceRangeDTO> getPriceRanges() {
        return priceRanges;
    }

    public void setPriceRanges(List<TmPriceRangeDTO> priceRanges) {
        this.priceRanges = priceRanges;
    }

    public TmEmbeddedDTO getEmbedded() {
        return embedded;
    }

    public void setEmbedded(TmEmbeddedDTO embedded) {
        this.embedded = embedded;
    }
}