package co.edu.unbosque.eventos.dto;

import java.util.Objects;
/**
 * DTO para la entidad TipoBoleta.
 * Se usa para enviar los tipos de boleta disponibles en un evento
 * sin exponer la relacion JPA con la entidad Evento.
 */
public class TipoBoletaDTO {

    /** Identificador unico del tipo de boleta. */
    private Long id;

    /** Nombre del tipo de boleta (ej. "General", "VIP", "Palco"). */
    private String nombre;

    /** Precio de la boleta. 0 si el evento es gratuito. */
    private Double precio;

    /** Cantidad de boletas disponibles para este tipo. */
    private Integer cantidadDisponible;

    /** Descripcion del tipo de boleta (beneficios, restricciones). */
    private String descripcion;

    /**
     * ID del evento al que pertenece este tipo de boleta.
     * Se usa al crear o actualizar un tipo de boleta via API.
     */
    private Long eventoId;

    /** Constructor por defecto. */
    public TipoBoletaDTO() {}

    /**
     * Constructor completo.
     *
     * @param id                 ID del tipo
     * @param nombre             Nombre del tipo
     * @param precio             Precio
     * @param cantidadDisponible Cantidad disponible
     * @param descripcion        Descripcion
     * @param eventoId           ID del evento asociado
     */
    public TipoBoletaDTO(Long id, String nombre, Double precio,
                         Integer cantidadDisponible, String descripcion, Long eventoId) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidadDisponible = cantidadDisponible;
        this.descripcion = descripcion;
        this.eventoId = eventoId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public Integer getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(Integer cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }

    @Override
    public int hashCode() { return Objects.hash(id, nombre, precio); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TipoBoletaDTO other = (TipoBoletaDTO) obj;
        return Objects.equals(id, other.id) && Objects.equals(nombre, other.nombre);
    }

    @Override
    public String toString() {
        return "TipoBoletaDTO [id=" + id + ", nombre=" + nombre + ", precio=" + precio + "]";
    }
}
