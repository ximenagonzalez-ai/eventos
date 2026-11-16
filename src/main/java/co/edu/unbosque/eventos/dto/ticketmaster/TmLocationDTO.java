package co.edu.unbosque.eventos.dto.ticketmaster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO que mapea el objeto "location" dentro de un venue de Ticketmaster.
 *
 * Ejemplo del JSON:
 * {
 *   "longitude": "-74.0821",
 *   "latitude":  "4.6097"
 * }
 *
 * IMPORTANTE: Ticketmaster devuelve lat/lng como String, no como Double.
 * En EventoApiClient las convertimos a Double al armar el LugarDTO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmLocationDTO {

    /** Longitud geografica como String (ej. "-74.0821"). */
    private String longitude;

    /** Latitud geografica como String (ej. "4.6097"). */
    private String latitude;

    public String getLongitude() { return longitude; }
    public void setLongitude(String longitude) { this.longitude = longitude; }

    public String getLatitude() { return latitude; }
    public void setLatitude(String latitude) { this.latitude = latitude; }
}
