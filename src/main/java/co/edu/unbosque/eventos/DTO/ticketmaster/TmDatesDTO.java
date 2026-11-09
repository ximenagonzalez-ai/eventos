package co.edu.unbosque.eventos.DTO.ticketmaster;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TmDatesDTO {

    private Start start;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class Start {
        private String localDate;
        private String localTime;
    }
}
