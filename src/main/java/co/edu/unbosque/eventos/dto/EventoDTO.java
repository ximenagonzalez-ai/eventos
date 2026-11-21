package co.edu.unbosque.eventos.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import co.edu.unbosque.eventos.model.Evento.EstadoEvento;
import co.edu.unbosque.eventos.model.Evento.FuenteEvento;
/**
 * DTO principal para la entidad Evento.
 * Incluye los datos del evento, el lugar con coordenadas (para la ruta)
 * y la categoria para el filtrado.
 */
public class EventoDTO {

    /** Identificador unico del evento. */
    private Long id;

    /** Nombre del evento. */
    private String nombre;

    /** Descripcion detallada del evento. */
    private String descripcion;

    /** Fecha y hora de inicio. Formato ISO: "2025-06-15T19:00:00" */
    private LocalDateTime fechaInicio;

    /** Fecha y hora de finalizacion. */
    private LocalDateTime fechaFin;

    /** URL de la imagen del evento. */
    private String imagenUrl;

    /** Estado del evento (ACTIVO, CANCELADO, FINALIZADO, PROXIMO). */
    private EstadoEvento estado;

    /**
     * Fuente del evento (LOCAL o EXTERNO).
     * Los eventos con fuente EXTERNO provienen de la API externa.
     */
    private FuenteEvento fuente;

    /**
     * ID externo del evento (de la API externa).
     * -- AQUI CONECTAS TU API EXTERNA: almacena el identificador que te da la API --
     */
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

    /** Nombre de la categoria (para mostrar en listados sin joins adicionales). */
    private String categoriaNombre;

    /**
     * DTO del lugar donde ocurre el evento.
     * Incluye latitud y longitud para que el frontend genere la ruta.
     */
    private LugarDTO lugar;

    /**
     * Lista de tipos de boleta disponibles.
     * El frontend usa esta lista para mostrar opciones de compra.
     */
    private List<TipoBoletaDTO> tiposBoleta;

    /** Constructor por defecto. */
    public EventoDTO() {}

    /**
     * Constructor basico (sin boletas ni lugar anidado).
     * Util para listados donde no se necesita el detalle completo.
     *
     * @param id             ID del evento
     * @param nombre         Nombre del evento
     * @param descripcion    Descripcion
     * @param fechaInicio    Fecha de inicio
     * @param fechaFin       Fecha de fin
     * @param imagenUrl      URL de imagen
     * @param estado         Estado del evento
     * @param fuente         Fuente (LOCAL/EXTERNO)
     * @param categoriaId    ID de la categoria
     * @param categoriaNombre Nombre de la categoria
     */
    public EventoDTO(Long id, String nombre, String descripcion, LocalDateTime fechaInicio,
                     LocalDateTime fechaFin, String imagenUrl, EstadoEvento estado,
                     FuenteEvento fuente, Long categoriaId, String categoriaNombre) {
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

    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }

    public LugarDTO getLugar() { return lugar; }
    public void setLugar(LugarDTO lugar) { this.lugar = lugar; }

    public List<TipoBoletaDTO> getTiposBoleta() { return tiposBoleta; }
    public void setTiposBoleta(List<TipoBoletaDTO> tiposBoleta) { this.tiposBoleta = tiposBoleta; }

    @Override
    public int hashCode() { return Objects.hash(id, nombre, fechaInicio); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EventoDTO other = (EventoDTO) obj;
        return Objects.equals(id, other.id) && Objects.equals(nombre, other.nombre);
    }

    @Override
    public String toString() {
        return "EventoDTO [id=" + id + ", nombre=" + nombre + ", estado=" + estado + "]";
    }
}
