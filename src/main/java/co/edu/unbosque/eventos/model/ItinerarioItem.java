package co.edu.unbosque.eventos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "itinerario_items")
public class ItinerarioItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "itinerario_id", nullable = false)
    private Itinerario itinerario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    public ItinerarioItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Itinerario getItinerario() { return itinerario; }
    public void setItinerario(Itinerario itinerario) { this.itinerario = itinerario; }

    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }
}