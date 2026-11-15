package co.edu.unbosque.eventos.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidad que representa un tipo de boleta para un evento.
 * Un evento puede tener varios tipos de boleta (ej. General, VIP, Palco).
 * Cada tipo tiene un precio y una cantidad disponible.
 */
@Entity
@Table(name = "tipo_boleta")
public class TipoBoleta {

    /** Identificador unico del tipo de boleta. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre del tipo de boleta (ej. "General", "VIP", "Palco"). */
    @Column(nullable = false)
    private String nombre;

    /**
     * Precio del tipo de boleta en pesos colombianos.
     * Puede ser 0 para eventos gratuitos.
     */
    @Column(nullable = false)
    private Double precio;

    /**
     * Cantidad total de boletas disponibles para este tipo.
     * Se usa para controlar el aforo.
     */
    @Column(nullable = false)
    private Integer cantidadDisponible;

    /** Descripcion adicional del tipo de boleta (beneficios, restricciones, etc.). */
    private String descripcion;

    /**
     * Evento al que pertenece este tipo de boleta.
     * Relacion ManyToOne: varios tipos de boleta pueden pertenecer a un evento.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    /** Constructor por defecto requerido por JPA. */
    public TipoBoleta() {}

    /**
     * Constructor completo sin id.
     *
     * @param nombre              Nombre del tipo de boleta
     * @param precio              Precio de la boleta
     * @param cantidadDisponible  Cantidad total disponible
     * @param descripcion         Descripcion del tipo
     * @param evento              Evento al que pertenece
     */
    public TipoBoleta(String nombre, Double precio, Integer cantidadDisponible,
                      String descripcion, Evento evento) {
        this.nombre = nombre;
        this.precio = precio;
        this.cantidadDisponible = cantidadDisponible;
        this.descripcion = descripcion;
        this.evento = evento;
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

    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }

    @Override
    public int hashCode() { return Objects.hash(id, nombre, precio); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TipoBoleta other = (TipoBoleta) obj;
        return Objects.equals(id, other.id) && Objects.equals(nombre, other.nombre);
    }

    @Override
    public String toString() {
        return "TipoBoleta [id=" + id + ", nombre=" + nombre + ", precio=" + precio + "]";
    }
}
