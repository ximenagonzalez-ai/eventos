package co.edu.unbosque.eventos.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;

@Entity
@Table(name = "evento")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    @Column(nullable = false)
    private LocalDateTime fechaFin;

    private String imagenUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEvento estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuenteEvento fuente;

    @Column(unique = true)
    private String idexterno;

    private String urlVentaExterna;

    private Double precioMinimoExterno;

    private Double precioMaximoExterno;

    private String monedaPrecioExterno;

    /**
     * Categoria del evento (Teatro, Feria, Concierto, etc.).
     * Relacion ManyToOne: varios eventos pueden tener la misma categoria.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lugar_id", nullable = false)
    private Lugar lugar;

    @OneToMany(
        mappedBy = "evento",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<TipoBoleta> tiposBoleta;

    // Precios externos informativos de Ticketmaster
    private Double precioMinimoExterno;
    private Double precioMaximoExterno;

    @Column(length = 10)
    private String monedaPrecioExterno;

    @Column(length = 2048)
    private String urlVentaExterna;

    public Evento() {}

    public Evento(
            String nombre,
            String descripcion,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            String imagenUrl,
            EstadoEvento estado,
            Categoria categoria,
            Lugar lugar) {

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

    public enum EstadoEvento {
        ACTIVO,
        CANCELADO,
        FINALIZADO,
        PROXIMO
    }

    public enum FuenteEvento {
        LOCAL,
        EXTERNO
    }

    public Long getId() { return id; }
    public void setId(Long v) { id = v; }

    public String getNombre() { return nombre; }
    public void setNombre(String v) { nombre = v; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String v) { descripcion = v; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime v) { fechaInicio = v; }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime v) { fechaFin = v; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String v) { imagenUrl = v; }

    public EstadoEvento getEstado() { return estado; }
    public void setEstado(EstadoEvento v) { estado = v; }

    public FuenteEvento getFuente() { return fuente; }
    public void setFuente(FuenteEvento v) { fuente = v; }

    public String getIdexterno() { return idexterno; }
    public void setIdexterno(String v) { idexterno = v; }

    public String getUrlVentaExterna() { return urlVentaExterna; }
    public void setUrlVentaExterna(String urlVentaExterna) { this.urlVentaExterna = urlVentaExterna; }

    public Double getPrecioMinimoExterno() { return precioMinimoExterno; }
    public void setPrecioMinimoExterno(Double precioMinimoExterno) { this.precioMinimoExterno = precioMinimoExterno; }

    public Double getPrecioMaximoExterno() { return precioMaximoExterno; }
    public void setPrecioMaximoExterno(Double precioMaximoExterno) { this.precioMaximoExterno = precioMaximoExterno; }

    public String getMonedaPrecioExterno() { return monedaPrecioExterno; }
    public void setMonedaPrecioExterno(String monedaPrecioExterno) { this.monedaPrecioExterno = monedaPrecioExterno; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria v) { categoria = v; }

    public Lugar getLugar() { return lugar; }
    public void setLugar(Lugar v) { lugar = v; }

    public List<TipoBoleta> getTiposBoleta() {
        return tiposBoleta;
    }

    public void setTiposBoleta(List<TipoBoleta> v) {
        tiposBoleta = v;
    }

    public Double getPrecioMinimoExterno() {
        return precioMinimoExterno;
    }

    public void setPrecioMinimoExterno(Double v) {
        precioMinimoExterno = v;
    }

    public Double getPrecioMaximoExterno() {
        return precioMaximoExterno;
    }

    public void setPrecioMaximoExterno(Double v) {
        precioMaximoExterno = v;
    }

    public String getMonedaPrecioExterno() {
        return monedaPrecioExterno;
    }

    public void setMonedaPrecioExterno(String v) {
        monedaPrecioExterno = v;
    }

    public String getUrlVentaExterna() {
        return urlVentaExterna;
    }

    public void setUrlVentaExterna(String v) {
        urlVentaExterna = v;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, fechaInicio);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Evento other)) return false;

        return Objects.equals(id, other.id)
            && Objects.equals(nombre, other.nombre);
    }

    @Override
    public String toString() {
        return "Evento [id=" + id
            + ", nombre=" + nombre
            + ", estado=" + estado + "]";
    }
}