package co.edu.unbosque.eventos.DTO;

import java.time.LocalDateTime;
import java.util.List;

import co.edu.unbosque.eventos.model.Itinerario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItinerarioDTO {

    // Datos que llegan al crear
    private Long usuarioId;
    private String nombre;

    // Datos que se devuelven
    private Long id;
    private LocalDateTime fechaCreacion;
    private List<String> eventos;

    public static ItinerarioDTO convertirAItinerarioDTO(Itinerario itinerario, List<String> eventos) {
        ItinerarioDTO dto = new ItinerarioDTO();
        dto.setId(itinerario.getId());
        dto.setUsuarioId(itinerario.getUsuario().getId());
        dto.setNombre(itinerario.getNombre());
        dto.setFechaCreacion(itinerario.getFechaCreacion());
        dto.setEventos(eventos);
        return dto;
    }
}