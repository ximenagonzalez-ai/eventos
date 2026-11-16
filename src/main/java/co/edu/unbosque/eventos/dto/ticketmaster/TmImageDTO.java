package co.edu.unbosque.eventos.dto.ticketmaster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO que mapea una imagen dentro de la respuesta de Ticketmaster.
 *
 * Ejemplo del JSON:
 * {
 *   "url":    "https://s1.ticketm.net/dam/a/123/imagen.jpg",
 *   "width":  640,
 *   "height": 427
 * }
 *
 * Un evento puede tener varias imagenes; en EventoApiClient tomamos
 * la primera de la lista para usarla como imagenUrl del EventoDTO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmImageDTO {

    /** URL publica de la imagen. */
    private String url;

    /** Ancho de la imagen en pixeles. */
    private int width;

    /** Alto de la imagen en pixeles. */
    private int height;

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
}
