package co.edu.unbosque.eventos.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Tipo de boleta para un evento específico.
 *
 * Un evento puede tener varios tipos: General, VIP, Palco, Estudiante, etc.
 * Cada tipo tiene su propio precio y cantidad disponible para controlar el aforo.
 *
 * Se gestionan desde el endpoint /tipoBoleta (solo ADMIN).
 * Si se borra el evento, sus tipos de boleta se borran también.
 */
@Entity
@Table(name = "tipo_boleta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoBoleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre del tipo, ej: "General", "VIP", "Palco". */
    @Column(nullable = false)
    private String nombre;

    /** Precio en pesos colombianos. Puede ser 0 para eventos gratuitos. */
    @Column(nullable = false)
    private Double precio;

    /** Cuántas boletas de este tipo quedan disponibles. */
    @Column(nullable = false)
    private Integer cantidadDisponible;

    /** Descripción: beneficios del tipo, restricciones de edad, etc. */
    private String descripcion;

    /** El evento al que pertenece esta boleta. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    public TipoBoleta(String nombre, Double precio, Integer cantidadDisponible,
                      String descripcion, Evento evento) {
        this.nombre = nombre;
        this.precio = precio;
        this.cantidadDisponible = cantidadDisponible;
        this.descripcion = descripcion;
        this.evento = evento;
    }
}
