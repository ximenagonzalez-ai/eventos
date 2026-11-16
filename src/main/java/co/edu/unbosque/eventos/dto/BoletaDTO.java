package co.edu.unbosque.eventos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BoletaDTO {

    private Long id;
    private Long compraId;
    private String codigo;
    private String tipoBoleta;
    private String evento;
    private BigDecimal precioPagado;
    private LocalDateTime fechaEmision;

    public BoletaDTO() {}

    public static BoletaDTO convertirABoletaDTO(Boleta boleta) {
        BoletaDTO dto = new BoletaDTO();
        dto.setId(boleta.getId());
        dto.setCompraId(boleta.getCompra().getId());
        dto.setCodigo(boleta.getCodigo());
        dto.setTipoBoleta(boleta.getTipoBoleta().getNombre());
        dto.setEvento(boleta.getTipoBoleta().getEvento().getNombre());
        dto.setPrecioPagado(boleta.getPrecioPagado());
        dto.setFechaEmision(boleta.getFechaEmision());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCompraId() { return compraId; }
    public void setCompraId(Long compraId) { this.compraId = compraId; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getTipoBoleta() { return tipoBoleta; }
    public void setTipoBoleta(String tipoBoleta) { this.tipoBoleta = tipoBoleta; }

    public String getEvento() { return evento; }
    public void setEvento(String evento) { this.evento = evento; }

    public BigDecimal getPrecioPagado() { return precioPagado; }
    public void setPrecioPagado(BigDecimal precioPagado) { this.precioPagado = precioPagado; }

    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }
}