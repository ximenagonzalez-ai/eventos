package co.edu.unbosque.eventos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "boletas")
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

    public Boleta() {}

    @PrePersist
    void antesDeGuardar() {
        this.fechaEmision = LocalDateTime.now();
        if (this.codigo == null) {
            this.codigo = UUID.randomUUID().toString();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Compra getCompra() { return compra; }
    public void setCompra(Compra compra) { this.compra = compra; }

    public TipoBoleta getTipoBoleta() { return tipoBoleta; }
    public void setTipoBoleta(TipoBoleta tipoBoleta) { this.tipoBoleta = tipoBoleta; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public BigDecimal getPrecioPagado() { return precioPagado; }
    public void setPrecioPagado(BigDecimal precioPagado) { this.precioPagado = precioPagado; }

    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }
}