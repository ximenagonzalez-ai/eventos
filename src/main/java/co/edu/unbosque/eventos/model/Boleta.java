package co.edu.unbosque.eventos.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "boletas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Boleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "compra_id", nullable = false)
    private Compra compra;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_boleta_id", nullable = false)
    private TipoBoleta tipoBoleta;

    @Column(nullable = false, unique = true, length = 36)
    private String codigo;

    @Column(name = "precio_pagado", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioPagado;

    @Column(name = "fecha_emision", nullable = false, updatable = false)
    private LocalDateTime fechaEmision;

    @PrePersist
    void antesDeGuardar() {
        this.fechaEmision = LocalDateTime.now();
        if (this.codigo == null) {
            this.codigo = UUID.randomUUID().toString();
        }
    }
}