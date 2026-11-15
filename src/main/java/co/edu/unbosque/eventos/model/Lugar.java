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
 * Entidad que representa el lugar fisico donde se realiza un evento.
 * Almacena coordenadas geograficas para poder generar la ruta hacia el lugar.
 */
@Entity
@Table(name = "lugar")
public class Lugar {

    /** Identificador unico del lugar. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre del lugar (ej. "Teatro Colon", "Parque El Tunal"). */
    @Column(nullable = false)
    private String nombre;

    /** Direccion textual del lugar (ej. "Calle 10 # 5-23, Bogota"). */
    @Column(nullable = false)
    private String direccion;

    /** Ciudad donde se encuentra el lugar. */
    @Column(nullable = false)
    private String ciudad;

    /**
     * Latitud geografica del lugar.
     * Se usa junto con longitud para mostrar la ruta en el mapa.
     * Ejemplo: 4.598889 (Bogota)
     */
    @Column(nullable = false)
    private Double latitud;

    /**
     * Longitud geografica del lugar.
     * Se usa junto con latitud para mostrar la ruta en el mapa.
     * Ejemplo: -74.076389 (Bogota)
     */
    @Column(nullable = false)
    private Double longitud;

    /** Descripcion adicional del lugar (capacidad, instalaciones, etc.). */
    private String descripcion;

    /**
     * Lista de eventos que se realizan en este lugar.
     * Se carga de forma perezosa para evitar consultas innecesarias.
     */
    @OneToMany(mappedBy = "lugar", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Evento> eventos;

    /** Constructor por defecto requerido por JPA. */
    public Lugar() {}

    /**
     * Constructor completo.
     *
     * @param nombre      Nombre del lugar
     * @param direccion   Direccion del lugar
     * @param ciudad      Ciudad del lugar
     * @param latitud     Latitud geografica
     * @param longitud    Longitud geografica
     * @param descripcion Descripcion opcional
     */
    public Lugar(String nombre, String direccion, String ciudad,
                 Double latitud, Double longitud, String descripcion) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.latitud = latitud;
        this.longitud = longitud;
        this.descripcion = descripcion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public List<Evento> getEventos() { return eventos; }
    public void setEventos(List<Evento> eventos) { this.eventos = eventos; }

    @Override
    public int hashCode() { return Objects.hash(id, nombre, latitud, longitud); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Lugar other = (Lugar) obj;
        return Objects.equals(id, other.id) && Objects.equals(nombre, other.nombre);
    }

    @Override
    public String toString() {
        return "Lugar [id=" + id + ", nombre=" + nombre + ", ciudad=" + ciudad + "]";
    }
}
