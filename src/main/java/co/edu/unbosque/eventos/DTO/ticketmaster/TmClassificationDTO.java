package co.edu.unbosque.eventos.DTO.ticketmaster;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TmClassificationDTO {

    private Segment segment;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class Segment {
        private String name;
    }
}
