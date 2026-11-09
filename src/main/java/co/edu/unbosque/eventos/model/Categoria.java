package co.edu.unbosque.eventos.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Categoría de un evento en nuestro catálogo.
 *
 * Ejemplos: "Teatro", "Concierto", "Feria", "Deporte", "Familiar", "Cine", "General".
 *
 * IMPORTANTE: cuando sincronizamos con Ticketmaster, el sistema busca automáticamente
 * las categorías por nombre (ej. si Ticketmaster dice "Music", busca "Concierto" en la BD).
 * Por eso hay que crear las categorías antes de hacer POST /evento/sync.
 *
 * Los nombres que Ticketmaster usa y cómo los mapeamos:
 *   "Arts & Theatre" → "Teatro"
 *   "Music"          → "Concierto"
 *   "Sports"         → "Deporte"
 *   "Family"         → "Familiar"
 *   "Film"           → "Cine"
 *   (otros)          → "General"
 */
@Entity
@Table(name = "categoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre único de la categoría. No puede haber dos con el mismo nombre. */
    @Column(unique = true, nullable = false)
    private String nombre;

    /** Descripción opcional para explicar qué tipo de eventos incluye esta categoría. */
    private String descripcion;

    /** Todos los eventos que pertenecen a esta categoría. */
    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Evento> eventos;

    public Categoria(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}
