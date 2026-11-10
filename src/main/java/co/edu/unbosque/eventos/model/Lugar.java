package co.edu.unbosque.eventos.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Lugar físico donde se realiza un evento.
 *
 * Además del nombre y dirección, guardamos la latitud y longitud
 * para que el frontend pueda mostrar la ubicación en Google Maps
 * o cualquier mapa y trazar la ruta desde donde está el usuario.
 *
 * Los lugares que vienen de Ticketmaster se crean automáticamente
 * cuando sincronizamos eventos. Si el lugar ya existe en la BD
 * (buscamos por nombre), no se crea de nuevo.
 */
@Entity
@Table(name = "lugar")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lugar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre del lugar, ej: "El Campín", "Teatro Mayor Julio Mario Santo Domingo". */
    @Column(nullable = false)
    private String nombre;

    /** Dirección completa del lugar, ej: "Cra 30 #57-60, Bogotá". */
    @Column(nullable = false)
    private String direccion;

    /** Ciudad donde está ubicado, ej: "Bogotá". Se usa para filtrar por ciudad. */
    @Column(nullable = false)
    private String ciudad;

    /**
     * Latitud geográfica, ej: 4.6482.
     * El frontend la usa junto con longitud para pintar el marcador en el mapa.
     */
    @Column(nullable = false)
    private Double latitud;

    /**
     * Longitud geográfica, ej: -74.0779.
     * El frontend la usa junto con latitud para pintar el marcador en el mapa.
     */
    @Column(nullable = false)
    private Double longitud;

    /** Información adicional: capacidad, cómo llegar, instalaciones, etc. */
    private String descripcion;

    /** Eventos que se realizan en este lugar. */
    @OneToMany(mappedBy = "lugar", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Evento> eventos;

    public Lugar(String nombre, String direccion, String ciudad,
                 Double latitud, Double longitud, String descripcion) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.latitud = latitud;
        this.longitud = longitud;
        this.descripcion = descripcion;
    }
}
