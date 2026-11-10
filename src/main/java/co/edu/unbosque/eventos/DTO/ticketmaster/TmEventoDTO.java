package co.edu.unbosque.eventos.DTO.ticketmaster;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TmEventoDTO {

    private String id;
    private String name;
    private List<TmImageDTO> images;
    private TmDatesDTO dates;
    private List<TmClassificationDTO> classifications;

    @JsonProperty("_embedded")
    private TmEmbeddedDTO embedded;
}
