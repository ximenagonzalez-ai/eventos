package co.edu.unbosque.eventos.DTO.ticketmaster;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TmEmbeddedDTO {

    @JsonProperty("events")
    private List<TmEventoDTO> events;

    @JsonProperty("venues")
    private List<TmVenueDTO> venues;
}
