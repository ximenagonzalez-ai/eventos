package co.edu.unbosque.eventos.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad principal de nuestro proyecto: representa un evento del catálogo.
 *
 * Un evento puede ser un concierto, teatro, feria, festival, etc.
 * Cada evento tiene una categoría (ej. "Concierto"), un lugar físico con
 * coordenadas para mostrar en el mapa, y tipos de boleta (General, VIP...).
 *
 * El campo "fuente" indica si lo creó un admin manualmente (LOCAL)
 * o si vino de Ticketmaster via sincronización automática (EXTERNO).
 *
 * El campo "idexterno" guarda el ID que Ticketmaster le asigna a cada evento,
 * así evitamos duplicados cuando sincronizamos varias veces.
 */
@Entity
@Table(name = "evento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre del evento, ej: "Festival Estéreo Picnic 2026". */
    @Column(nullable = false)
    private String nombre;

    /** Descripción larga del evento que se muestra en la vista de detalle. */
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /** Cuándo empieza el evento. */
    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    /** Cuándo termina. Si viene de Ticketmaster, calculamos +3 horas de estimado. */
    @Column(nullable = false)
    private LocalDateTime fechaFin;

    /** URL de la imagen de portada. Puede ser externa (Ticketmaster) o subida por el admin. */
    private String imagenUrl;

    /**
     * Estado actual del evento:
     * - ACTIVO: visible y con venta de boletas abierta
     * - PROXIMO: anunciado pero aún no en venta
     * - CANCELADO: se canceló
     * - FINALIZADO: ya ocurrió
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEvento estado;

    /**
     * Origen del evento:
     * - LOCAL: lo creó un administrador desde Swagger/frontend
     * - EXTERNO: se sincronizó automáticamente desde Ticketmaster
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private FuenteEvento fuente = FuenteEvento.LOCAL;

    /**
     * ID del evento en Ticketmaster. Lo usamos para no guardar el mismo evento dos veces
     * cuando hacemos POST /evento/sync varias veces seguidas.
     */
    @Column(unique = true)
    private String idexterno;

    /**
     * Categoría del evento (Teatro, Concierto, Feria, Deporte...).
     * Para crear un evento necesitas primero crear la categoría via POST /categoria/create.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    /**
     * Lugar donde se realiza el evento. Tiene latitud y longitud
     * para que el frontend pueda mostrar la ubicación en el mapa.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lugar_id", nullable = false)
    private Lugar lugar;

    /**
     * Tipos de boleta del evento (General, VIP, Palco...).
     * Se gestionan por separado via el TipoBoletaService.
     * Si borras el evento, se borran sus tipos de boleta también (orphanRemoval).
     */
    @OneToMany(mappedBy = "evento", fetch = FetchType.LAZY,
               cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TipoBoleta> tiposBoleta;

    /** Estados posibles de un evento en nuestro catálogo. */
    public enum EstadoEvento {
        ACTIVO, CANCELADO, FINALIZADO, PROXIMO
    }

    /** Si lo creamos nosotros es LOCAL; si vino de Ticketmaster es EXTERNO. */
    public enum FuenteEvento {
        LOCAL, EXTERNO
    }
}
