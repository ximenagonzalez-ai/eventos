package co.edu.unbosque.eventos.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Entidad principal que representa un evento del catalogo.
 * Puede ser un teatro, feria, concierto, exposicion, etc.
 * Se relaciona con Categoria, Lugar y TipoBoleta.
 */
@Entity
@Table(name = "evento")
public class Evento {

    /** Identificador unico del evento. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre del evento (ej. "Festival de Verano 2025"). */
    @Column(nullable = false)
    private String nombre;

    /** Descripcion detallada del evento. */
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /** Fecha y hora de inicio del evento. */
    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    /** Fecha y hora de finalizacion del evento. */
    @Column(nullable = false)
    private LocalDateTime fechaFin;

    /**
     * URL de la imagen de portada del evento.
     * Puede ser una URL externa (API de imagenes) o una ruta interna.
     */
    private String imagenUrl;

    /**
     * Estado actual del evento.
     * ACTIVO: visible y con venta de boletas abierta.
     * CANCELADO: el evento fue cancelado.
     * FINALIZADO: el evento ya ocurrio.
     * PROXIMO: anunciado pero aun no en venta.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEvento estado;

    /**
     * Fuente del evento: LOCAL (creado manualmente por admin)
     * o EXTERNO (sincronizado desde una API externa).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuenteEvento fuente;

    /**
     * ID del evento en la API externa (si aplica).
     * Se usa para evitar duplicados al sincronizar.
     * -- AQUI CONECTAS TU API EXTERNA: guarda el id que te devuelva la API --
     */
    @Column(unique = true)
    private String idexterno;

    /**
     * Categoria del evento (Teatro, Feria, Concierto, etc.).
     * Relacion ManyToOne: varios eventos pueden tener la misma categoria.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    /**
     * Lugar donde se realiza el evento.
     * Relacion ManyToOne: varios eventos pueden realizarse en el mismo lugar.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lugar_id", nullable = false)
    private Lugar lugar;

    /**
     * Lista de tipos de boleta disponibles para este evento.
     * Relacion OneToMany: un evento puede tener varios tipos (General, VIP, etc.).
     */
    @OneToMany(mappedBy = "evento", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TipoBoleta> tiposBoleta;

    /** Constructor por defecto requerido por JPA. */
    public Evento() {}

    /**
     * Constructor completo para eventos locales.
     *
     * @param nombre      Nombre del evento
     * @param descripcion Descripcion del evento
     * @param fechaInicio Fecha y hora de inicio
     * @param fechaFin    Fecha y hora de fin
     * @param imagenUrl   URL de la imagen
     * @param estado      Estado del evento
     * @param categoria   Categoria del evento
     * @param lugar       Lugar del evento
     */
    public Evento(String nombre, String descripcion, LocalDateTime fechaInicio,
                  LocalDateTime fechaFin, String imagenUrl, EstadoEvento estado,
                  Categoria categoria, Lugar lugar) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.imagenUrl = imagenUrl;
        this.estado = estado;
        this.fuente = FuenteEvento.LOCAL;
        this.categoria = categoria;
        this.lugar = lugar;
    }

    /**
     * Estados posibles de un evento.
     */
    public enum EstadoEvento {
        /** Evento activo, visible y con boletas disponibles */
        ACTIVO,
        /** Evento cancelado */
        CANCELADO,
        /** Evento ya ocurrio */
        FINALIZADO,
        /** Evento anunciado pero sin venta abierta */
        PROXIMO
    }

    /**
     * Fuente de origen del evento.
     */
    public enum FuenteEvento {
        /** Creado manualmente por el administrador */
        LOCAL,
        /**
         * Sincronizado desde una API externa.
         * -- AQUI CONECTAS TU API EXTERNA: los eventos importados usan esta fuente --
         */
        EXTERNO
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public EstadoEvento getEstado() { return estado; }
    public void setEstado(EstadoEvento estado) { this.estado = estado; }

    public FuenteEvento getFuente() { return fuente; }
    public void setFuente(FuenteEvento fuente) { this.fuente = fuente; }

    public String getIdexterno() { return idexterno; }
    public void setIdexterno(String idexterno) { this.idexterno = idexterno; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public Lugar getLugar() { return lugar; }
    public void setLugar(Lugar lugar) { this.lugar = lugar; }

    public List<TipoBoleta> getTiposBoleta() { return tiposBoleta; }
    public void setTiposBoleta(List<TipoBoleta> tiposBoleta) { this.tiposBoleta = tiposBoleta; }

    @Override
    public int hashCode() { return Objects.hash(id, nombre, fechaInicio); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Evento other = (Evento) obj;
        return Objects.equals(id, other.id) && Objects.equals(nombre, other.nombre);
    }

    @Override
    public String toString() {
        return "Evento [id=" + id + ", nombre=" + nombre + ", estado=" + estado + "]";
    }
}
