package co.edu.unbosque.eventos.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import co.edu.unbosque.eventos.model.Evento.EstadoEvento;
import co.edu.unbosque.eventos.model.Evento.FuenteEvento;

public class EventoDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String imagenUrl;
    private EstadoEvento estado;
    private FuenteEvento fuente;
    private String idexterno;

    private String urlVentaExterna;

    private Double precioMinimoExterno;

    private Double precioMaximoExterno;

    private String monedaPrecioExterno;

    /**
     * ID de la categoria a la que pertenece el evento.
     * Se usa al crear o actualizar un evento.
     */
    private Long categoriaId;
    private String categoriaNombre;
    private LugarDTO lugar;
    private List<TipoBoletaDTO> tiposBoleta;

    // Información externa de Ticketmaster
    private Double precioMinimoExterno;
    private Double precioMaximoExterno;
    private String monedaPrecioExterno;
    private String urlVentaExterna;

    public EventoDTO() {}

    public EventoDTO(
            Long id,
            String nombre,
            String descripcion,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            String imagenUrl,
            EstadoEvento estado,
            FuenteEvento fuente,
            Long categoriaId,
            String categoriaNombre) {

        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.imagenUrl = imagenUrl;
        this.estado = estado;
        this.fuente = fuente;
        this.categoriaId = categoriaId;
        this.categoriaNombre = categoriaNombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public EstadoEvento getEstado() {
        return estado;
    }

    public void setEstado(EstadoEvento estado) {
        this.estado = estado;
    }

    public FuenteEvento getFuente() {
        return fuente;
    }

    public void setFuente(FuenteEvento fuente) {
        this.fuente = fuente;
    }

    public String getIdexterno() {
        return idexterno;
    }

    public void setIdexterno(String idexterno) {
        this.idexterno = idexterno;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public String getUrlVentaExterna() { return urlVentaExterna; }
    public void setUrlVentaExterna(String urlVentaExterna) { this.urlVentaExterna = urlVentaExterna; }

    public Double getPrecioMinimoExterno() { return precioMinimoExterno; }
    public void setPrecioMinimoExterno(Double precioMinimoExterno) { this.precioMinimoExterno = precioMinimoExterno; }

    public Double getPrecioMaximoExterno() { return precioMaximoExterno; }
    public void setPrecioMaximoExterno(Double precioMaximoExterno) { this.precioMaximoExterno = precioMaximoExterno; }

    public String getMonedaPrecioExterno() { return monedaPrecioExterno; }
    public void setMonedaPrecioExterno(String monedaPrecioExterno) { this.monedaPrecioExterno = monedaPrecioExterno; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public LugarDTO getLugar() {
        return lugar;
    }

    public void setLugar(LugarDTO lugar) {
        this.lugar = lugar;
    }

    public List<TipoBoletaDTO> getTiposBoleta() {
        return tiposBoleta;
    }

    public void setTiposBoleta(List<TipoBoletaDTO> tiposBoleta) {
        this.tiposBoleta = tiposBoleta;
    }

    public Double getPrecioMinimoExterno() {
        return precioMinimoExterno;
    }

    public void setPrecioMinimoExterno(Double precioMinimoExterno) {
        this.precioMinimoExterno = precioMinimoExterno;
    }

    public Double getPrecioMaximoExterno() {
        return precioMaximoExterno;
    }

    public void setPrecioMaximoExterno(Double precioMaximoExterno) {
        this.precioMaximoExterno = precioMaximoExterno;
    }

    public String getMonedaPrecioExterno() {
        return monedaPrecioExterno;
    }

    public void setMonedaPrecioExterno(String monedaPrecioExterno) {
        this.monedaPrecioExterno = monedaPrecioExterno;
    }

    public String getUrlVentaExterna() {
        return urlVentaExterna;
    }

    public void setUrlVentaExterna(String urlVentaExterna) {
        this.urlVentaExterna = urlVentaExterna;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, fechaInicio);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EventoDTO other)) return false;

        return Objects.equals(id, other.id)
                && Objects.equals(nombre, other.nombre);
    }

    @Override
    public String toString() {
        return "EventoDTO [id=" + id
                + ", nombre=" + nombre
                + ", estado=" + estado + "]";
    }
}