package co.edu.unbosque.eventos.dto;

import java.util.Objects;

/**
 * DTO (Data Transfer Object) para la entidad Categoria.
 * Se usa para enviar y recibir datos de categorias en los endpoints REST
 * sin exponer directamente la entidad JPA.
 */
public class CategoriaDTO {

    /** Identificador unico de la categoria. */
    private Long id;

    /** Nombre de la categoria (ej. "Teatro", "Feria", "Concierto"). */
    private String nombre;

    /** Descripcion de la categoria. */
    private String descripcion;

    /** Constructor por defecto. */
    public CategoriaDTO() {}

    /**
     * Constructor con todos los campos.
     *
     * @param id          ID de la categoria
     * @param nombre      Nombre de la categoria
     * @param descripcion Descripcion de la categoria
     */
    public CategoriaDTO(Long id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public int hashCode() { return Objects.hash(id, nombre); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CategoriaDTO other = (CategoriaDTO) obj;
        return Objects.equals(id, other.id) && Objects.equals(nombre, other.nombre);
    }

    @Override
    public String toString() {
        return "CategoriaDTO [id=" + id + ", nombre=" + nombre + "]";
    }
}
