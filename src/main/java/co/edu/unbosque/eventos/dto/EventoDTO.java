package co.edu.unbosque.eventos.dto;

import co.edu.unbosque.eventos.model.Evento.EstadoEvento;
import co.edu.unbosque.eventos.model.Evento.FuenteEvento;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String imagenUrl;
    private EstadoEvento estado;
    private FuenteEvento fuente;

    /** ID del evento en la fuente externa (ej. Ticketmaster). */
    private String idexterno;

    /** ID de la categoría en la BD (para crear/actualizar). */
    private Long categoriaId;

    /** Nombre de la categoría (para respuestas). */
    private String categoriaNombre;

    /** Lugar con latitud/longitud para mostrar en mapa. */
    private LugarDTO lugar;

    /** Tipos de boleta disponibles para este evento. */
    private List<TipoBoletaDTO> tiposBoleta;
}
