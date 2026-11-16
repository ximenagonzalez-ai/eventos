package co.edu.unbosque.eventos.dto.ticketmaster;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO que mapea el objeto "address" dentro de un venue de Ticketmaster.
 *
 * Ejemplo del JSON:
 * {
 *   "line1": "Calle 10 # 5-32"
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmAddressDTO {

    /** Primera linea de la direccion (calle y numero). */
    private String line1;

    public String getLine1() { return line1; }
    public void setLine1(String line1) { this.line1 = line1; }
}

