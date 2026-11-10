package co.edu.unbosque.eventos.DTO;

import co.edu.unbosque.eventos.model.Rol;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {

    private Long id;
    private String nombre;
    private String email;
    private String contrasena;   // solo para crear/actualizar, nunca se devuelve
    private Rol rol;
    private boolean activo;
    private boolean emailVerificado;
}
