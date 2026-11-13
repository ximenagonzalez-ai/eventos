package co.edu.unbosque.eventos.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoBoletaDTO {

    private Long id;
    private String nombre;
    private Double precio;
    private Integer cantidadDisponible;
    private String descripcion;
    private Long eventoId;
}
