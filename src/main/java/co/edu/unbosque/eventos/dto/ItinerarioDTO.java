package co.edu.unbosque.eventos.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ItinerarioDTO {

    // Datos que llegan al crear
    private Long usuarioId;
    private String nombre;

    // Datos que se devuelven
    private Long id;
    private LocalDateTime fechaCreacion;
    private List<String> eventos;

    public ItinerarioDTO() {}

    public static ItinerarioDTO convertirAItinerarioDTO(Itinerario itinerario, List<String> eventos) {
        ItinerarioDTO dto = new ItinerarioDTO();
        dto.setId(itinerario.getId());
        dto.setUsuarioId(itinerario.getUsuario().getId());
        dto.setNombre(itinerario.getNombre());
        dto.setFechaCreacion(itinerario.getFechaCreacion());
        dto.setEventos(eventos);
        return dto;
    }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public List<String> getEventos() { return eventos; }
    public void setEventos(List<String> eventos) { this.eventos = eventos; }
}