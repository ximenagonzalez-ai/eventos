package co.edu.unbosque.eventos.model;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Entidad que representa una categoria de evento.
 * Ejemplos: Teatro, Feria, Concierto, Exposicion, etc.
 */
@Entity
@Table(name = "categoria")
public class Categoria {

    /** Identificador unico de la categoria. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre unico de la categoria (ej. "Teatro", "Feria"). */
    @Column(unique = true, nullable = false)
    private String nombre;

    /** Descripcion opcional de la categoria. */
    private String descripcion;

    /**
     * Lista de eventos que pertenecen a esta categoria.
     * Se carga de forma perezosa para evitar consultas innecesarias.
     */
    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Evento> eventos;

    /** Constructor por defecto requerido por JPA. */
    public Categoria() {}

    /**
     * Constructor con nombre y descripcion.
     *
     * @param nombre      Nombre de la categoria
     * @param descripcion Descripcion de la categoria
     */
    public Categoria(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public List<Evento> getEventos() { return eventos; }
    public void setEventos(List<Evento> eventos) { this.eventos = eventos; }

    @Override
    public int hashCode() { return Objects.hash(id, nombre); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Categoria other = (Categoria) obj;
        return Objects.equals(id, other.id) && Objects.equals(nombre, other.nombre);
    }

    @Override
    public String toString() {
        return "Categoria [id=" + id + ", nombre=" + nombre + "]";
    }
}
