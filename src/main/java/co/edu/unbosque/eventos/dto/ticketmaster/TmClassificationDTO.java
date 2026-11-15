package co.edu.unbosque.eventos.dto.ticketmaster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO que mapea el objeto "classifications[0]" de un evento de Ticketmaster.
 * Contiene el genero/tipo del evento, que usamos para asignar la categoria.
 *
 * Ejemplo del JSON:
 * {
 *   "segment": { "name": "Arts & Theatre" },
 *   "genre":   { "name": "Theatre" }
 * }
 *
 * En EventoApiClient usamos "segment.name" o "genre.name" para
 * determinar que categoria asignarle al evento en nuestra BD.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmClassificationDTO {

    /**
     * Segmento principal del evento (ej. "Arts & Theatre", "Music", "Sports").
     * Se usa para mapear a nuestras categorias (Teatro, Concierto, etc.).
     */
    private TmSegmentDTO segment;

    /**
     * Genero mas especifico del evento (ej. "Theatre", "Rock", "Football").
     */
    private TmSegmentDTO genre;

    public TmSegmentDTO getSegment() { return segment; }
    public void setSegment(TmSegmentDTO segment) { this.segment = segment; }

    public TmSegmentDTO getGenre() { return genre; }
    public void setGenre(TmSegmentDTO genre) { this.genre = genre; }

    // -----------------------------------------------------------------------

    /**
     * DTO anidado para "segment" y "genre" que solo tienen "name".
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TmSegmentDTO {

        /** Nombre del segmento o genero (ej. "Arts & Theatre", "Theatre"). */
        private String name;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
