package co.edu.unbosque.eventos.dto.ticketmaster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO que mapea el objeto "city" dentro de un venue de Ticketmaster.
 *
 * Ejemplo del JSON:
 * {
 *   "name": "Bogota"
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmCityDTO {

    /** Nombre de la ciudad donde esta el venue. */
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
