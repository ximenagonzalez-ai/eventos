package co.edu.unbosque.eventos.dto.ticketmaster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO que mapea el objeto "dates" de un evento de Ticketmaster.
 *
 * Ejemplo del JSON:
 * {
 *   "start": {
 *     "localDate": "2025-08-15",
 *     "localTime": "20:00:00"
 *   }
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmDatesDTO {

    /**
     * Objeto que contiene la fecha y hora de inicio del evento.
     * Ticketmaster los devuelve separados: fecha en "localDate" y hora en "localTime".
     */
    private TmStartDTO start;

    public TmStartDTO getStart() { return start; }
    public void setStart(TmStartDTO start) { this.start = start; }

    // -----------------------------------------------------------------------

    /**
     * DTO anidado que mapea el objeto "start" dentro de "dates".
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TmStartDTO {

        /**
         * Fecha local del evento en formato "yyyy-MM-dd".
         * Ejemplo: "2025-08-15"
         */
        private String localDate;

        /**
         * Hora local del evento en formato "HH:mm:ss".
         * Ejemplo: "20:00:00"
         * Puede ser null si Ticketmaster no la informa.
         */
        private String localTime;

        public String getLocalDate() { return localDate; }
        public void setLocalDate(String localDate) { this.localDate = localDate; }

        public String getLocalTime() { return localTime; }
        public void setLocalTime(String localTime) { this.localTime = localTime; }
    }
}

