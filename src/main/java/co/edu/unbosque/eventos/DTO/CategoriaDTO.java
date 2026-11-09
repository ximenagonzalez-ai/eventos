package co.edu.unbosque.eventos.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaDTO {

    private Long id;
    private String nombre;
    private String descripcion;
}
