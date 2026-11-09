package co.edu.unbosque.eventos.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LugarDTO {

    private Long id;
    private String nombre;
    private String direccion;
    private String ciudad;
    private Double latitud;
    private Double longitud;
    private String descripcion;
}
