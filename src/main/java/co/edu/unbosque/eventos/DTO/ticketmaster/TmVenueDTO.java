package co.edu.unbosque.eventos.DTO.ticketmaster;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TmVenueDTO {

    private String name;
    private Address address;
    private City city;
    private TmLocationDTO location;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class Address {
        private String line1;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class City {
        private String name;
    }
}
