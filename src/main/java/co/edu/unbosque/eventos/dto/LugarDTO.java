package co.edu.unbosque.eventos.dto;

import java.util.Objects;

/**
 * DTO para la entidad Lugar.
 * Incluye latitud y longitud para que el frontend pueda
 * mostrar el lugar en un mapa y calcular la ruta para llegar.
 */
public class LugarDTO {

    /** Identificador unico del lugar. */
    private Long id;

    /** Nombre del lugar (ej. "Teatro Colon"). */
    private String nombre;

    /** Direccion textual del lugar. */
    private String direccion;

    /** Ciudad donde se encuentra el lugar. */
    private String ciudad;

    /**
     * Latitud geografica.
     * El frontend usa este valor junto con longitud para mostrar el mapa
     * y generar la ruta de llegada (Google Maps, OpenStreetMap, etc.).
     */
    private Double latitud;

    /**
     * Longitud geografica.
     * El frontend usa este valor junto con latitud para mostrar el mapa.
     */
    private Double longitud;

    /** Descripcion adicional del lugar. */
    private String descripcion;

    /** Constructor por defecto. */
    public LugarDTO() {}

    /**
     * Constructor completo.
     *
     * @param id          ID del lugar
     * @param nombre      Nombre del lugar
     * @param direccion   Direccion del lugar
     * @param ciudad      Ciudad del lugar
     * @param latitud     Latitud geografica
     * @param longitud    Longitud geografica
     * @param descripcion Descripcion opcional
     */
    public LugarDTO(Long id, String nombre, String direccion, String ciudad,
                    Double latitud, Double longitud, String descripcion) {
        this.id = id;
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

    @Override
    public int hashCode() { return Objects.hash(id, nombre); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LugarDTO other = (LugarDTO) obj;
        return Objects.equals(id, other.id) && Objects.equals(nombre, other.nombre);
    }

    @Override
    public String toString() {
        return "LugarDTO [id=" + id + ", nombre=" + nombre + ", ciudad=" + ciudad + "]";
    }
}

