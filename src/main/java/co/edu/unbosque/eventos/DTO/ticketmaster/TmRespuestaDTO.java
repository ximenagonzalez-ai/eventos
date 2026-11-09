package co.edu.unbosque.eventos.DTO.ticketmaster;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TmRespuestaDTO {

    @JsonProperty("_embedded")
    private TmEmbeddedDTO embedded;
}
