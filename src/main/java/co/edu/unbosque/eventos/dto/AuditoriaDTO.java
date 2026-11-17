package co.edu.unbosque.eventos.dto;

import java.time.LocalDateTime;

import co.edu.unbosque.eventos.model.Auditoria;

public class AuditoriaDTO {

    private Long id;
    private Long usuarioId;
    private String accion;
    private String entidad;
    private Long entidadId;
    private String detalle;
    private LocalDateTime fechaRegistro;

    public AuditoriaDTO() {}

    public static AuditoriaDTO convertirAAuditoriaDTO(Auditoria auditoria) {
        AuditoriaDTO dto = new AuditoriaDTO();
        dto.setId(auditoria.getId());
        if (auditoria.getUsuario() != null) {
            dto.setUsuarioId(auditoria.getUsuario().getId());
        }
        dto.setAccion(auditoria.getAccion());
        dto.setEntidad(auditoria.getEntidad());
        dto.setEntidadId(auditoria.getEntidadId());
        dto.setDetalle(auditoria.getDetalle());
        dto.setFechaRegistro(auditoria.getFechaRegistro());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    public String getEntidad() { return entidad; }
    public void setEntidad(String entidad) { this.entidad = entidad; }

    public Long getEntidadId() { return entidadId; }
    public void setEntidadId(Long entidadId) { this.entidadId = entidadId; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}