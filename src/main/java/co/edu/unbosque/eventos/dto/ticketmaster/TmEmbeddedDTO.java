package co.edu.unbosque.eventos.dto.ticketmaster;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO que mapea el objeto "_embedded" dentro de un evento de Ticketmaster.
 * Contiene los venues (lugares) donde ocurre el evento.
 *
 * Ejemplo del JSON:
 * {
 *   "_embedded": {
 *     "venues": [
 *       {
 *         "name": "Teatro Colon",
 *         "address": { "line1": "Calle 10 # 5-32" },
 *         "city":    { "name": "Bogota" },
 *         "location": { "longitude": "-74.0821", "latitude": "4.6097" }
 *       }
 *     ]
 *   }
 * }
 *
 * Un evento puede tener varios venues pero normalmente es solo uno.
 * Usamos venues.get(0) en EventoApiClient.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmEmbeddedDTO {

    /**
     * Lista de venues del evento.
     * Tomamos el primero (index 0) para armar el LugarDTO.
     */
    private List<TmVenueDTO> venues;

    public List<TmVenueDTO> getVenues() { return venues; }
    public void setVenues(List<TmVenueDTO> venues) { this.venues = venues; }
}

