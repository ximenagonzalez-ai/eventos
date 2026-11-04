package co.edu.unbosque.eventos.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import co.edu.unbosque.eventos.model.Rol;
import co.edu.unbosque.eventos.model.Usuario;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String email;
    private Rol rol;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    
    





	public static UsuarioResponse convertirAusuarioResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.isActivo(),
                usuario.getFechaCreacion()
        );
    }
}
