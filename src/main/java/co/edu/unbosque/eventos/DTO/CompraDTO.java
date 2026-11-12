package co.edu.unbosque.eventos.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import co.edu.unbosque.eventos.model.Compra;
import co.edu.unbosque.eventos.model.EstadoCompra;

public class CompraDTO {

    // Datos que llegan al comprar
    private Long usuarioId;
    private Long tipoBoletaId;
    private Integer cantidad;

    // Datos que se devuelven
    private Long id;
    private BigDecimal total;
    private EstadoCompra estado;
    private LocalDateTime fechaCompra;

    public CompraDTO() {}

    public static CompraDTO convertirACompraDTO(Compra compra) {
        CompraDTO dto = new CompraDTO();
        dto.setId(compra.getId());
        dto.setUsuarioId(compra.getUsuario().getId());
        dto.setTotal(compra.getTotal());
        dto.setEstado(compra.getEstado());
        dto.setFechaCompra(compra.getFechaCompra());
        return dto;
    }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getTipoBoletaId() { return tipoBoletaId; }
    public void setTipoBoletaId(Long tipoBoletaId) { this.tipoBoletaId = tipoBoletaId; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public EstadoCompra getEstado() { return estado; }
    public void setEstado(EstadoCompra estado) { this.estado = estado; }

    public LocalDateTime getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(LocalDateTime fechaCompra) { this.fechaCompra = fechaCompra; }
}